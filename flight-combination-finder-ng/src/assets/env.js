(function (window) {
    window["env"] = window["env"] || {};

    // Environment variables
    window["env"]["production"] = false;
    window["env"]["backendUrl"] = "http://localhost:8080/backend";
    window["env"]["debug"] = true;
})(this);