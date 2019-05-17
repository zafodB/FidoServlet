<%--
Made by Filip Adamik on 17/05/2019
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Sign-in | FIDO demo</title>
</head>

<style>
    .content {
        max-width: 300px;
        margin: auto;
    }
</style>

<script src="javaScriptFiles/decoder.js"></script>
<script src="javaScriptFiles/signinPage.js"></script>

<body>
<div class="content">
    <p style="text-align:right"><a href="https://fidoserver.ml/AAFidoServer/register">Register</a></p>
    <br>
    <br>

    E-mail:

    <br>
    <br>
    <input type="email" id="email">

    <br>
    <br>
    <button id="sign-in-button">
        Sign-in
    </button>

    <br>
    <br>
    <span id="outtext">
    This is empty.
    </span>

</div>

</body>
</html>
