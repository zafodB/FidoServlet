document.getElementById("make-credential-button").addEventListener("click", registerNewCredential);


function registerNewCredential() {
    _fetch('/SecondFidoTest/DoABarrelRoll', {}).then(options => {

        const makeCredentialOptions = {};
    _options = options;

    // console.log(str2ab(options.user.id));
    // console.log(str2ab(options.challenge));

    makeCredentialOptions.rp = options.rp;

    makeCredentialOptions.user = options.user;


    makeCredentialOptions.user.id = str2ab(options.user.id);
    makeCredentialOptions.challenge = str2ab(options.challenge);
    makeCredentialOptions.pubKeyCredParams = options.pubKeyCredParams;

    // console.log(makeCredentialOptions);

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
    if ('rawId' in attestation) {
        publicKeyCredential.rawId = binToStr(attestation.rawId);
    }
    if (!attestation.response) {
        showErrorMsg("Make Credential response lacking 'response' attribute");
    }

    const response = {};
    response.clientDataJSON = binToStr(attestation.response.clientDataJSON);
    response.attestationObject = binToStr(attestation.response.attestationObject);

    // Check if transports are included in the registration response.
    if (attestation.response.getTransports) {
        response.transports = attestation.response.getTransports();
    }

    publicKeyCredential.response = response;

    return _fetch('/DoATrick', {
        data: JSON.stringify(publicKeyCredential),
        session: _options.session.id
    });

    })
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

function prepareChallenge(challenge) {
    encoded = b64EncodeUnicode(challenge)
    return encoded.substring(0, encoded.length - 2)
}

function strToBin(str) {
    return Uint8Array.from(atob(str), c => c.charCodeAt(0));
}

function b64EncodeUnicode(str) {
    // first we use encodeURIComponent to get percent-encoded UTF-8,
    // then we convert the percent encodings into raw bytes which
    // can be fed into btoa.
    return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g,
        function toSolidBytes(match, p1) {
            return String.fromCharCode('0x' + p1);
        }));
}

function stringToByteArray(str) {
    var ch, st, re = [];
    for (var i = 0; i < str.length; i++ ) {
        ch = str.charCodeAt(i);  // get char
        st = [];                 // set up "stack"
        do {
            st.push( ch & 0xFF );  // push byte to stack
            ch = ch >> 8;          // shift value down by 1 byte
        }
        while ( ch );
        // add stack contents to result
        // done because chars have "wrong" endianness
        re = re.concat( st.reverse() );
    }
    // return an array of bytes
    return re;
}

function str2ab(str) {
    console.log(str);
    var buf = new ArrayBuffer(str.length * 2); // 2 bytes for each char
    var bufView = new Uint16Array(buf);
    for (var i = 0, strLen = str.length; i < strLen; i++) {
        bufView[i] = str.charCodeAt(i);
    }
    return buf;
}