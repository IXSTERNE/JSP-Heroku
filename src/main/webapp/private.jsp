<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Protected</title>
</head>
<body>
    <h1>This is a protected page <%= session.getAttribute("username") %>!</h1>
</body>
</html>