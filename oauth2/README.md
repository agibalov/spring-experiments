An attempt to build a minimal application that implements both the OAuth2 Authorization Service and the OAuth2 Resource Server.

```bash
curl -X POST http://MyClientId1:MyClientId1Secret@localhost:8080/oauth/token \
  -d grant_type=password \
  -d username=user1 \
  -d password=user1password

{
  "access_token": "e5ed41c5-dbea-42d1-8bf3-d85942c56fdb",
  "token_type": "bearer",
  "refresh_token": "eb008d5f-632f-4691-8f1e-801a081ac5a0",
  "expires_in":43199,
  "scope":"read"
}

curl -X GET http://localhost:8080/ \
  -H "Authorization: Bearer e5ed41c5-dbea-42d1-8bf3-d85942c56fdb"

{"message":"PRINCIPAL is user 'user1' ([ROLE_USER])"}
```
