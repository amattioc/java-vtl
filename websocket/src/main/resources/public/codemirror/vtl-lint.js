(function (mod) {
    if (typeof exports == "object" && typeof module == "object") // CommonJS
        mod(require("../../lib/codemirror"));
    else if (typeof define == "function" && define.amd) // AMD
        define(["../../lib/codemirror"], mod);
    else // Plain browser env
        mod(CodeMirror);
})(function (CodeMirror) {
    "use strict";

    CodeMirror.registerHelper("lint", "vtl", function (text, callback) {
        var found = [];

        // {message, severity, from, to}
        fetch('/validate', {
            method: "POST",
            body: text
        }).then(function (response) {
            return response.json();
        }).then(function (json) {
            var errors = [];
            for (var i = 0; i < json.length; i++) {
                errors.push({
                    from: CodeMirror.Pos(json[i].line - 1, json[i].column),
                    to: CodeMirror.Pos(json[i].line - 1, json[i].column + 1),
                    message: json[i].message,
                    severity: "error"
                });
            }
            callback(errors, false);
        }).catch(function (error) {
            // handle network error
            alert(error);
        });
    });

});
