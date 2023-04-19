# Cavisto
Telegram bot for saving and advising wine

Plan:
1. Extract data about vines
   1. For example, vivino.com is a good data source, but it requires data parsing for search.
   Try to search wine Baron d'Arignac, requires parsing wine-card__name link-color-alt-grey
    ```bash
   curl https://www.vivino.com/search/wines\?q\=baron%20d
   ```
   ```html
    <span class='header-smaller text-block wine-card__name'>
    <a class="link-color-alt-grey" data-cartitemsource="text-search" href="/wines/1474107"><span class='bold'>
    <mark>Baron</mark> <mark>d</mark>'Arignac Vin Rouge
    </span>
    ```
   But we can get data about countries
   ```bash
    curl https://www.vivino.com/api/countries
    ```
   And even use API for suggestions, for example wines of France with min rating and price range. Just what I want.
    ```
    "https://www.vivino.com/api/explore/explore",
    params = {
        "country_code": "FR",
        "country_codes[]":"pt",
        "currency_code":"EUR",
        "grape_filter":"varietal",
        "min_rating":"1",
        "order_by":"price",
        "order":"asc",
        "page": 1,
        "price_range_max":"500",
        "price_range_min":"0",
        "wine_type_ids[]":"1"
    },
    ```
   ```bash
   curl 'https://www.vivino.com/api/explore/explore?country_code=FR&currency_code=EUR&min_rating=3&price_range_mix=7&price_range_max=20&order_by=price&order=asc'
    ```
2. Setup database and write controllers for CRUD operations   

3. Add telegram bot interaction
   1. Save user info to Postgres db
   2. Save user wines (formalize name using vivino api), support photo uploading
   3. Add '/advice' command with parameters, suggest user preferences based on saved vines
   4. Add '/top' command to get most popular wines among users
   5. Add '/review' command for writing user's personal review
   6. ...



Detailed Plan:
- [x] Init project
- [x] Write code for parsing vivino.com
- [ ] Setup database with docker-compose and connect to it
- [ ] Write functions for db usage
- [x] Integrate with telegram
