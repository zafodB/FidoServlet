<%--
Made by Filip Adamik on 17/05/2019
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Register | FIDO Demo</title>
</head>

<style>
    .content {
        max-width: 300px;
        margin: auto;
    }
</style>

<script src="javaScriptFiles/registerPage.js"></script>

<body>
<div class="content">
    <p style="text-align:right"><a href="https://fidoserver.ml/AAFidoServer/">Sign-in</a></p>
    <br>
    <br>

    Name:
    <br>
    <br>
    <input type="text" id="userDisplayName">

    <br>
    <br>
    E-mail:
    <br>
    <br>
    <input type="email" id="email">

    <br>
    <br>
    <button id="make-credential-button">
        Register
    </button>

    <br>
    <br>
    <span id="outtext">
    This is empty.
    </span>

</div>

</body>
</html>
