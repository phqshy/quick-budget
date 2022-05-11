const bodyParser = require('body-parser');
const express = require('express');
const app = express();
const port = 1984;
const db = require('quick.db');
const axios = require("axios");

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

async function startRequest(){
        let resp = await axios.get("https://v6.exchangerate-api.com/v6/key/latest/USD");
        let data = resp.data;
        await db.set("exchange_usd", data);

        resp = await axios.get("https://v6.exchangerate-api.com/v6/key/latest/GBP");
        data = resp.data;
        await db.set("exchange_gbp", data);

        resp = await axios.get("https://v6.exchangerate-api.com/v6/key/latest/EUR");
        data = resp.data;
        await db.set("exchange_eur", data);

        resp = await axios.get("https://v6.exchangerate-api.com/v6/key/latest/JPY");
        data = resp.data;
        await db.set("exchange_jpy", data);
        setTimeout(() => {
                startRequest();
        }, 1 * 1000 * 60 * 60 * 24);
}

startRequest();


app.get('/exchange/usd', async (req, res) => {
        const data = await db.get("exchange_usd");
        res.send(data);
});
app.get("/exchange/gbp", async (req, res) => {
        const data = await db.get("exchange_gbp");
        res.send(data);
});
app.get("/exchange/eur", async (req, res) => {
        const data = await db.get("exchange_eur");
        res.send(data);
});

app.get("/exchange/jpy", async (req, res) => {
        const data = await db.get("exchange_jpy");
        res.send(data);
});

app.post("/create_user", async (req, res) => {
        try{
                const json = req.body;
                console.log(json);
                if (await db.has(json.username)) {
                        res.send("Account exists");
                        console.log("Attempted to create an account that already exists");
                        return;
                }

                console.log("Creating account");
                await db.set(json.username, json);
                console.log("Account created successfully");
                res.send("Success");
        } catch (err) {
                console.log(err);
                res.send(err);
                return;
        }
});

app.get("/login", async (req, res) => {
        try{
                const username = req.query.username;
                const password = req.query.password;
                console.log(username);
                console.log(password);
                if (await db.has(username)){
                        console.log(typeof await db.get(username));
                        const json = await db.get(username);
                        if (json.password == password){
                                res.send(await db.get(`${username}`));
                                return;
                        }
                }
                res.send("Invalid credentials");
        } catch (err) {
                console.log(err);
                res.send("Internal server error");
        }
});

app.listen(port, () => {
        console.log("app online");
});
