const API = "http://localhost:9090";



window.register = async function () {

    const username = document.getElementById("regUsername").value;
    const email = document.getElementById("regEmail").value;
    const password = document.getElementById("regPassword").value;

    const status = document.getElementById("registerStatus");

    try {

        status.innerText = "Registering...";

        const res = await fetch(`${API}/api/auth/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, email, password })
        });

        const data = await res.json().catch(() => ({}));

        status.innerText = res.ok
            ? "Registered successfully"
            : (data.message || "Error");

    } catch (err) {
        console.error(err);
        status.innerText = "Server error";
    }
};


window.login = async function () {

    const username = document.getElementById("loginUsername").value;
    const password = document.getElementById("loginPassword").value;

    const status = document.getElementById("loginStatus");

    try {

        const res = await fetch(`${API}/api/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ usernameOrEmail: username, password })
        });

        const data = await res.json().catch(() => ({}));

        if (res.ok && data.accessToken) {

            localStorage.setItem("token", data.accessToken);

            status.innerText = "Login successful... redirecting";

            setTimeout(() => {
                window.location.href = "compiler.html";
            }, 800);

        } else {
            status.innerText = data.message || "Login failed";
        }

    } catch (err) {
        console.error(err);
        status.innerText = "Server error";
    }
};


window.logout = function () {
    localStorage.removeItem("token");
    window.location.href = "login.html";
}

window.submitFormula = async function () {

    const token = localStorage.getItem("token");
    const status = document.getElementById("compilerStatus");

    if (!token) {
        status.innerText = "Login first";
        return;
    }

    const formula = document.getElementById("formulaInput").value;

    try {

   
        const parseRes = await fetch(`${API}/parse-with-result`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ formula })
        });

        const data = await parseRes.json().catch(() => ({}));

        if (!parseRes.ok) {
            status.innerText = "Parse failed";
            return;
        }

        const {
            tokens = [],
            ast = null,
            astTree = null,
            finalResult = null,
            errors = []
        } = data;

        
        document.getElementById("tokens").innerHTML =
            tokens.length
                ? tokens.map(t =>
                    `<div>🔹 <b>${t.type}</b> : ${t.lexeme || ""}</div>`
                ).join("")
                : "No tokens";

       
        document.getElementById("tree").innerText =
    ast
        ? JSON.stringify(ast, null, 2)
        : "No AST";

       
        document.getElementById("astJson").innerText =
            ast
                ? JSON.stringify(ast, null, 2)
                : "No AST";

        
        document.getElementById("result").innerText =
    finalResult !== null && finalResult !== undefined
        ? `Result = ${finalResult}`
        : "No result";

       
        document.getElementById("errors").innerHTML =
            errors.length
                ? errors.map(e =>
                    `<div>❌ ${e.message || e.code || "Error"}</div>`
                ).join("")
                : "No errors";

        

        status.innerText = "";

    
        if (typeof loadHistory === "function") {
            await loadHistory();
        }

    } catch (err) {
        console.error(err);

       
        status.innerText = "Request failed";
    }
};

function renderHistory(items) {

    const container = document.getElementById("history");

    console.log("RENDER ITEMS:", items);

    if (!items || items.length === 0) {
        container.innerText = "No history found";
        return;
    }

    container.innerHTML = items.map((item, index) => `
        <div>
            <b>#${index + 1}</b><br/>
            ${item.formula}
        </div>
    `).join("");
}

async function loadHistory() {

    const token = localStorage.getItem("token");

    try {
        const res = await fetch(`${API}/api/history`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        const data = await res.json();

        console.log("HISTORY RAW RESPONSE:", data);

        
        const items = Array.isArray(data)
            ? data
            : (data.items || []);

        renderHistory(items);

    } catch (err) {
        console.error("History error:", err);
    }
}
window.onload = function () {
    loadHistory();
};