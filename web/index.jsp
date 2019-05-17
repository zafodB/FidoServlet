<%--
Made by Filip Adamik on 17/05/2019
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>FIDO demo: Sign-in</title>
</head>

<style>
    .content {
        max-width: 500px;
        margin: auto;
    }
</style>

<script src="javaScriptFiles/decoder.js"></script>
<script src="javaScriptFiles/signinPage.js"></script>

<body>
<div class="content">

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
