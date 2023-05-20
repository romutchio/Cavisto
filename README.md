# Cavisto
[![Scala CI](https://github.com/romutchio/Cavisto/actions/workflows/scala.yml/badge.svg)](https://github.com/romutchio/Cavisto/actions/workflows/scala.yml)
[![codecov](https://codecov.io/gh/romutchio/Cavisto/branch/main/graph/badge.svg?token=Z2VUNNWO1I)](https://codecov.io/gh/romutchio/Cavisto)  
Telegram bot for saving and advising wine

### Plan:
1. Extract data about vines
   - For example, vivino.com is a good data source, but it requires data parsing for search.
     For example, search request for wine Baron d'Arignac.
     ```bash
     > curl https://www.vivino.com/search/wines\?q\=baron%20d

     <span class='header-smaller text-block wine-card__name'>
     <a class="link-color-alt-grey" data-cartitemsource="text-search" href="/wines/1474107"><span class='bold'>
     <mark>Baron</mark> <mark>d</mark>'Arignac Vin Rouge
     </span>
     ```
   - Get data about countries
     ```bash
     > curl https://www.vivino.com/api/countries
     ```
   - Get wine suggestions, for example wines of France with min rating and price range. Just what I want.
     ```bash
     > curl 'https://www.vivino.com/api/explore/explore?country_code=FR&currency_code=EUR&min_rating=3&price_range_mix=7&price_range_max=20&order_by=price&order=asc'
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
2. Setup database and write controllers for CRUD operations   

3. Add telegram bot interaction
   - Save user info to Postgres db
   - Save user wines (formalize name using vivino api), support photo uploading
   - Add '/advise' command with parameters, suggest user preferences based on saved vines
   - Add '/top' command to get most popular wines among users (Optional)
   - Add '/note' command for writing user's personal note about wine
   - Add '/search' command to search wines by name
   - Add '/notes' command to get all user's notes



### TODO:
- [x] Init project
- [x] Write code for parsing vivino.com
- [x] Setup database with docker-compose and connect to it
- [x] Write functions for db usage
- [x] Integrate with telegram
  - [x] Implement '/advise' command
  - [x] Implement '/note' command
  - [x] Implement '/search' command
  - [x] Implement '/notes' command 


### Commands' showcase:
- `/start`  
  <img src="docs/command_start.png" width=400>

- `/help`  
  <img src="docs/command_help.png" width=400> 

- `/advise`  
  <img src="docs/command_advise_init.png" width=400>
  - `Country` button  
  <img src="docs/command_advise_country.png" width=400>
  - `Type` button  
  <img src="docs/command_advise_type.png" width=400>
  - `Price` buttons  
  <img src="docs/command_advise_price.png" width=400>
  - `Advise` button  
  <img src="docs/command_advise_advise.png" width=400>
  - `Next` button  
  <img src="docs/command_advise_next.png" width=400>

- `/note`  
  <img src="docs/command_note_init.png" width=400>
  - `Edit wine name` button  
  <img src="docs/command_note_edit_wine_name.png" width=400>
  - `Edit Rating` button  
  <img src="docs/command_note_edit_rating.png" width=400>
  - `Edit Review` button  
  <img src="docs/command_note_edit_review.png" width=400>
  - `Save` button  
  <img src="docs/command_note_save.png" width=400>

- `/notes`  
  - User's list of notes is empty  
  <img src="docs/command_notes_init.png" width=400>  
  - User's list of notes is not empty (max 10 on page)  
  <img src="docs/command_notes_page_0.png" width=400>
  <img src="docs/command_notes_page_1.png" width=400>
  - `10` button click (You can edit note)  
  <img src="docs/command_notes_edit.png" width=400>

- `/search cabernet`  
  <img src="docs/command_search_cabernet.png" width=400>