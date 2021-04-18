(function () {

    function Pb(msg) {
        console.info(msg)
    }
    function wc(a, b) {
        u = window;
        l = u.navigator;
        f = u.document;
        J = f.currentScript || Array.from(f.getElementsByTagName("script")).pop();
        v = (0 == (J && J.src || "").indexOf("http:") ? "http:" : "https:") + "//172.168.30.62:8088/ddm/adj/" + b + ".js";
        v = '<script src="' + v + '">\x3c/script>';
        f.write(v)
    }
    function nc() {
        var c = window.document;
        c.body || (c.write('<b style="display:none;">_</b>'), c.body || Pb("Body element does not exist"));
        c = c.getElementsByClassName ? c.getElementsByClassName("dcmads") : c.getElementsByTagName("ins");
        var m = c && c[0].getAttribute("data-dcm-placement")
        m ? wc(c, m) : Pb("No ad ins element found")
    }
    nc();
}).call(this);
