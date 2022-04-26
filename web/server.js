const bodyParser = require('body-parser');
const express = require('express');
const app = express();
const port = 1984;
const db = require('quick.db');
const axios = require("axios");

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

async function startRequest(){
        let resp = await axios.get("https://v6.exchangerate-api.com/v6/0a71d4fb1406cbde584f17b7/latest/USD");
        let data = resp.data;
        await db.set("exchange_usd", data);

        resp = await axios.get("https://v6.exchangerate-api.com/v6/0a71d4fb1406cbde584f17b7/latest/GBP");
        data = resp.data;
        await db.set("exchange_gbp", data);

        resp = await axios.get("https://v6.exchangerate-api.com/v6/0a71d4fb1406cbde584f17b7/latest/EUR");
        data = resp.data;
        await db.set("exchange_eur", data);

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

app.post("/updateuser", async (req, res) => {
        console.log(req.body.uuid);
        res.send("accepted!");
});

app.listen(port, () => {
        console.log("app online");
});
