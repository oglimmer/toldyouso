#!/usr/bin/env bash
set -euo pipefail

# toldyouso — Build & Deploy Script
# Usage: ./oglimmer.sh [build|release|show] [options]

# --- Configuration ---
APP_NAME="toldyouso"
REGISTRY="${REGISTRY:-registry.oglimmer.com}"
IMAGE_NAME="${REGISTRY}/${APP_NAME}"

# --- Colors ---
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

info()    { echo -e "${BLUE}[INFO]${NC} $*"; }
success() { echo -e "${GREEN}[SUCCESS]${NC} $*"; }
warn()    { echo -e "${YELLOW}[WARNING]${NC} $*"; }
error()   { echo -e "${RED}[ERROR]${NC} $*" >&2; }

# --- Defaults ---
COMMAND="build"
VERBOSE=false
NO_PUSH=false
DRY_RUN=false
PLATFORM="auto"
REGISTRIES=""

usage() {
    cat <<EOF
Usage: $(basename "$0") [command] [options]

Commands:
  build       Build and deploy (default)
  release     Create a new release with version bumping
  show        Show current version

Options:
  -v, --verbose          Verbose output
  --no-push              Skip pushing to registry
  --dry-run              Show what would be done
  --registries "R1,R2"   Push to multiple registries
  --platform PLATFORM    Target platform (amd64|arm64|multi|auto)
  -h, --help             Show this help
EOF
}

# --- Parse arguments ---
while [[ $# -gt 0 ]]; do
    case $1 in
        build|release|show) COMMAND="$1"; shift ;;
        -v|--verbose) VERBOSE=true; shift ;;
        --no-push) NO_PUSH=true; shift ;;
        --dry-run) DRY_RUN=true; shift ;;
        --registries) REGISTRIES="$2"; shift 2 ;;
        --platform) PLATFORM="$2"; shift 2 ;;
        -h|--help) usage; exit 0 ;;
        *) error "Unknown option: $1"; usage; exit 1 ;;
    esac
done

# --- Prerequisites ---
check_prerequisites() {
    local missing=()
    for cmd in docker mvn git; do
        command -v "$cmd" &>/dev/null || missing+=("$cmd")
    done
    if [[ ${#missing[@]} -gt 0 ]]; then
        error "Missing required tools: ${missing[*]}"
        exit 1
    fi
    if ! docker info &>/dev/null; then
        error "Docker daemon is not running"
        exit 1
    fi
}

# --- Version helpers ---
get_version() {
    mvn help:evaluate -Dexpression=project.version -q -DforceStdout 2>/dev/null
}

show_version() {
    info "Version: $(get_version)"
}

bump_version() {
    local current
    current=$(get_version)
    local base="${current%-SNAPSHOT}"

    IFS='.' read -r major minor patch <<< "$base"

    echo ""
    info "Current version: $current"
    echo "  1) patch  → $major.$minor.$((patch + 1))"
    echo "  2) minor  → $major.$((minor + 1)).0"
    echo "  3) major  → $((major + 1)).0.0"
    read -rp "Select version bump [1]: " choice
    choice="${choice:-1}"

    case $choice in
        1) new_version="$major.$minor.$((patch + 1))" ;;
        2) new_version="$major.$((minor + 1)).0" ;;
        3) new_version="$((major + 1)).0.0" ;;
        *) error "Invalid choice"; exit 1 ;;
    esac

    info "Bumping to $new_version"

    if [[ "$DRY_RUN" == true ]]; then
        info "[DRY-RUN] Would set version to $new_version"
        return
    fi

    # Set release version
    mvn versions:set -DnewVersion="$new_version" -q
    mvn versions:commit -q

    # Git commit & tag
    git add pom.xml
    git commit -m "Release v$new_version"
    git tag "v$new_version"

    success "Released v$new_version"

    # Build the release
    build_image "$new_version"

    # Set next SNAPSHOT
    local next_snapshot="$major.$minor.$((patch + 2))-SNAPSHOT"
    mvn versions:set -DnewVersion="$next_snapshot" -q
    mvn versions:commit -q
    git add pom.xml
    git commit -m "Prepare next development iteration $next_snapshot"

    success "Next development version: $next_snapshot"
}

# --- Resolve platform ---
resolve_platform() {
    case "$PLATFORM" in
        amd64) echo "linux/amd64" ;;
        arm64) echo "linux/arm64" ;;
        multi) echo "linux/amd64,linux/arm64" ;;
        auto)
            local arch
            arch=$(uname -m)
            case "$arch" in
                x86_64)  echo "linux/amd64" ;;
                aarch64|arm64) echo "linux/arm64" ;;
                *) echo "linux/amd64" ;;
            esac
            ;;
        *) echo "$PLATFORM" ;;
    esac
}

# --- Build Docker image ---
build_image() {
    local tag="${1:-latest}"
    local platform
    platform=$(resolve_platform)

    info "Building Docker image: ${IMAGE_NAME}:${tag} (platform: ${platform})"

    if [[ "$DRY_RUN" == true ]]; then
        info "[DRY-RUN] Would build ${IMAGE_NAME}:${tag}"
        return
    fi

    # Collect all image tags
    local tags=("--tag" "${IMAGE_NAME}:${tag}")
    if [[ "$tag" != "latest" ]]; then
        tags+=("--tag" "${IMAGE_NAME}:latest")
    fi

    # Additional registries
    if [[ -n "$REGISTRIES" ]]; then
        IFS=',' read -ra extra_regs <<< "$REGISTRIES"
        for reg in "${extra_regs[@]}"; do
            tags+=("--tag" "${reg}/${APP_NAME}:${tag}")
            if [[ "$tag" != "latest" ]]; then
                tags+=("--tag" "${reg}/${APP_NAME}:latest")
            fi
        done
    fi

    if [[ "$platform" == *","* ]]; then
        # Multi-platform build requires push
        if [[ "$NO_PUSH" == true ]]; then
            warn "Multi-platform build requires --push; ignoring --no-push"
        fi
        docker buildx build \
            --platform "$platform" \
            "${tags[@]}" \
            --push \
            .
        success "Multi-platform image built and pushed"
    else
        docker buildx build \
            --platform "$platform" \
            "${tags[@]}" \
            --load \
            .
        success "Image built: ${IMAGE_NAME}:${tag}"

        if [[ "$NO_PUSH" != true ]]; then
            push_image "$tag"
        fi
    fi
}

# --- Push image ---
push_image() {
    local tag="${1:-latest}"

    info "Pushing ${IMAGE_NAME}:${tag}"
    docker push "${IMAGE_NAME}:${tag}"
    if [[ "$tag" != "latest" ]]; then
        docker push "${IMAGE_NAME}:latest"
    fi

    # Push to additional registries
    if [[ -n "$REGISTRIES" ]]; then
        IFS=',' read -ra extra_regs <<< "$REGISTRIES"
        for reg in "${extra_regs[@]}"; do
            info "Pushing to ${reg}/${APP_NAME}:${tag}"
            docker push "${reg}/${APP_NAME}:${tag}"
            if [[ "$tag" != "latest" ]]; then
                docker push "${reg}/${APP_NAME}:latest"
            fi
        done
    fi

    success "Image pushed"
}

# --- Main ---
check_prerequisites

case "$COMMAND" in
    build)
        build_image
        ;;
    release)
        bump_version
        ;;
    show)
        show_version
        ;;
esac
