REST API corresponds to the project task.

Additionally added API for registration, administration and rating:
  access for all users
    POST /auth                        register new user ( User {id: Integer/UID, first_name: String, last_name: String,
                                                                password: String, email: String, created_at: Date, role: Enum} )
    GET  /confirm/{confirmationCode}  confirmation of registration link

  access for role "ADMIN" only
    GET  /admin/posts                 show all unchecked posts
    PUT  /admin/posts/{postID}        set unchecked post as checked
    GET  /admin/comments              show all unchecked comments
    PUT  /admin/comments/{commentID}  set comments as approved

  access for all roles (authorized users)
    GET  /rating/{postID}             get individual trader's rating
    GET  /rating                      get common rating

notes: for "PUT /object/:id" field "status" must be "CHECKED" or "UNCHECKED" only
           "POST /object"    field "status" must be "CHECKED" or "UNCHECKED" only
           "POST/auth"       field "role"   must be "ADMIN", "TRADER" or "USER" only


Used database: REDIS, POSTGRESQL. Settings in hibernate.cfg.xml, redis.properties
Postgresql database structure in file init.sql (attached to project)

notes: for testing purposes, lifetime of links for registration and password changing reduced to 25 seconds.
