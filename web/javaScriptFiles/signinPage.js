window.onload = function () {
    document.getElementById("sign-in-button").addEventListener("click", registerNewCredential);
}

function registerNewCredential() {

    var email = document.getElementById("email").value;

    if (verifyEmail(email)) {
        document.getElementById("email").style.backgroundColor = "white";

        let _parameters;
        _fetch('/SecondFidoTest/signin', {}).then(parameters => {

                const requestOptions = {};
                _parameters = parameters;

                requestOptions.challenge = str2ab(parameters.challenge);
                if ('timeout' in parameters) {
                    requestOptions.timeout = parameters.timeout;
                }
                if ('rpId' in parameters) {
                    requestOptions.rpId = parameters.rpId;
                }
                if ('allowCredentials' in parameters) {
                    requestOptions.allowCredentials = credentialListConversion(parameters.allowCredentials);
                }

                console.log(requestOptions);

                return navigator.credentials.get({
                    "publicKey": requestOptions
                });

            }).then(assertion => {
                const publicKeyCredential = {};

                if ('id' in assertion) {
                    publicKeyCredential.id = assertion.id;
                }
                if ('type' in assertion) {
                    publicKeyCredential.type = assertion.type;
                }
                if ('rawId' in assertion) {
                    publicKeyCredential.rawId = binToStr(assertion.rawId);
                }
                if (!assertion.response) {
                    throw "Get assertion response lacking 'response' attribute";
                }

                const _response = assertion.response;

                publicKeyCredential.response = {
                    clientDataJSON:     binToStr(_response.clientDataJSON),
                    authenticatorData:  binToStr(_response.authenticatorData),
                    signature:          binToStr(_response.signature),
                    userHandle:         binToStr(_response.userHandle)
                };

                //TODO finish this up
                return _fetch('/FinishGetAssertion', {
                    data: JSON.stringify(publicKeyCredential),
                    session: _parameters.session.id
                });

            }).then(result => {
                console.log(result);

                if (result && result.success) {
                    //TODO Do some fancy shit when they log in.

                    // let tmpName = $('#username-text').value;
                    // $('#instructions').textContent = 'Thanks, ' + tmpName + '! Login was a success.';
                    // hide('#auth-spinner');
                }
            }).catch(err => {
            //TODO handle error
            });

    } else {
        document.getElementById("email").style.backgroundColor = "red";

        return writeOutText("Invalid email.");
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
            // console.log(response.body)
            return response.json();
        } else {
            throw response.statusText;
        }
    });
};

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
