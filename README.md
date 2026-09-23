# LifeORA — Module 1

## Current flow

LifeORA now has **one public first page**: `frontend/index.html`.

It works like a simple marketplace home page:
- Visitors can see demo/available items without logging in.
- The page tries to fetch real items from the Java backend.
- If the database has no items yet (or the backend is temporarily unavailable), demo products are displayed so the interface is never empty.
- Login and Register are available from the top navigation.
- After successful login, the user is sent to `marketplace.html`.
- There is no separate dashboard page in this version.

## Logo

Put the real JPEG logo here:

`frontend/images/LOGO.jpg`

The `.jpg` extension is used on the home, login, register, and marketplace pages.

## Backend

Run from the `backend` folder:

```cmd
javac -cp "lib\mysql-connector-j-26.7.0.jar" *.java
java -cp ".;lib\mysql-connector-j-26.7.0.jar" Main
```

Keep the backend CMD window open.

## Architecture

HTML + CSS + JavaScript
        ↓
HTTP request
        ↓
Java backend
        ↓
JDBC
        ↓
MySQL

The public home page can fetch real products through `GET /items`.
