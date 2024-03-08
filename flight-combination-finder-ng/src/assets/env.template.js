(function (window) {
    window["env"] = window["env"] || {};

    // Environment variables
    window["env"]["production"] = "${PRODUCTION}";
    window["env"]["backendURL"] = "${BACKEND_URL}s";
    window["env"]["debug"] = "${DEBUG}";
})(this);