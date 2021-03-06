window.onload = function () {
    document.getElementById("make-credential-button").addEventListener("click", registerNewCredential);
};

function registerNewCredential() {

    var email = document.getElementById("email").value;
    var name = document.getElementById("userDisplayName").value;

    if (verifyEmail(email) && name.length != 0) {
        document.getElementById("email").style.backgroundColor = "white";

        _fetch('/AAFidoServer/begin-registration', {
            uniqueName: email,
            displayName: name
        }).then(options => {

            const makeCredentialOptions = {};
            _options = options;

            makeCredentialOptions.rp = options.rp;

            makeCredentialOptions.user = options.user;


            makeCredentialOptions.user.id = str2ab(options.user.id);
            makeCredentialOptions.challenge = str2ab(options.challenge);
            makeCredentialOptions.pubKeyCredParams = options.pubKeyCredParams;

            return navigator.credentials.create({
                "publicKey": makeCredentialOptions
            });

        }).then(attestation => {

            const publicKeyCredential = {};

            if ('id' in attestation) {
                publicKeyCredential.id = attestation.id;
            }
            if ('type' in attestation) {
                publicKeyCredential.type = attestation.type;
            }
            if ('clientExtensionResults' in attestation) {
                publicKeyCredential.clientExtensionResults = attestation.clientExtensionResults;
            } else {
                publicKeyCredential.clientExtensionResults = {};
            }
            if (!attestation.response) {
                showErrorMsg("Make Credential response lacking 'response' attribute");
            }

            const response = {};
            response.clientDataJSON = binToStr(attestation.response.clientDataJSON);
            response.attestationObject = binToStr(attestation.response.attestationObject);

            publicKeyCredential.response = response;


            return _fetch('/AAFidoServer/finish-registration', {
                keyData: JSON.stringify(publicKeyCredential),
                userUniqueName: email,
                userDisplayName: name
            })
        }).then(myResponse => {
            var str = "";
            writeOutText(str.concat("the response is: ", myResponse.result));
        }).catch(reason => {
            writeOutText(reason);
        });

    } else {
        document.getElementById("email").style.backgroundColor = "red";

        return writeOutText("Invalid email or user name.");
    }
}

function _fetch(url, obj) {
    let headers = new Headers({
        'Content-Type': 'application/x-www-form-urlencoded'
    });
    let body;
    if (typeof URLSearchParams === "function") {
        body = new URLSearchParams();
        for (let key in obj) {
            body.append(key, obj[key]);
        }
        // Set body to string value to handle an Edge case
        body = body.toString();
    } else {
        // Add parameters to body manually if browser doesn't support URLSearchParams
        body = "";
        for (let key in obj) {
            body += encodeURIComponent(key) + "=" + encodeURIComponent(obj[key]) + "&";
        }
    }
    return fetch(url, {
        method: 'POST',
        headers: headers,
        credentials: 'include',
        body: body
    }).then(response => {
        if (response.status === 200) {
            return response.json();
        } else if (response.status === 409) {
            throw "Email already exists.";
        } else if (response.status === 403) {
            throw "The Registration Request Record was not found in the database or the response doesn't match.";
        } else {
            throw response.statusText;
        }
    });
}

function binToStr(bin) {
    return btoa(new Uint8Array(bin).reduce(
        (s, byte) => s + String.fromCharCode(byte), ''
    ));
}

function str2ab(str) {

    var bytes = [];

    for (var i = 0; i < str.length; ++i) {
        var code = str.charCodeAt(i);

        bytes = bytes.concat([code]);
    }

    var uint8Array = new Uint8Array(bytes.length);
    for (var j = 0; j < uint8Array.length; j++) {
        uint8Array[j] = bytes[j];
    }

    return uint8Array;

}

function writeOutText(str) {
    var spanXyz = document.getElementById("outtext");
    spanXyz.innerHTML = str;
}

function verifyEmail(email) {
    var regex = /[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/;
    return regex.test(email);
}
