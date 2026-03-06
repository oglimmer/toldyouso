# toldyouso - ... and i told you so ;)

Ever wanted to keep evidence of an "and I knew it" situation? Save your early wisdom with an unmodifiable date and time and so secure a proof that you had known it ... and tell'em: AND I TOLD YOU SO!

How to run it
-------------

1.) Install docker

2.) `docker compose up` (starts CouchDB, Redis, and the app)

3.) Browse to http://localhost:8080

Architecture
------------

- **Database:** CouchDB for persistent storage of users and entries
- **Session store:** Redis via [Redisson](https://github.com/redisson/redisson) Tomcat session manager — HTTP sessions are stored in Redis instead of in-memory, allowing the app to scale horizontally across multiple replicas without sticky sessions
- **UI:** JSF (Mojarra) with BootsFaces components

Kubernetes deployment
---------------------

A Helm chart is provided in `helm/toldyouso/`. Key configuration in `values.yaml`:

- `redis.host` / `redis.port` — Redis instance for session storage
- `appConfig` — application properties (CouchDB connection, SMTP, domain)
- `replicaCount` — can be scaled to multiple replicas since sessions are shared via Redis

Build & deploy
--------------

`./oglimmer.sh --help`
