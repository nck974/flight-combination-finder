(function (window) {
    window["env"] = window["env"] || {};

    // Environment variables
    window["env"]["production"] = "${PRODUCTION}";
    window["env"]["backendUrl"] = "${BACKEND_URL}";
    window["env"]["debug"] = "${DEBUG}";
})(this);