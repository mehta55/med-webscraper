# med-webscraper
A web scrapper developed in JAVA to scrap price & other details of medicines from different websites over the web and compare them.
Uses ElasticSearch to store all medicines data and provide auto-complete and suggestions feature to users searching for medicine by name.

## APIs:
  - For Medicine resource:
  
     - GET `/medicine/search?keyword=:medicineName` => To search for medicine using its name or a keyword (provides auto-complete & suggestion).
     - GET `/medicine/price?id=:id` => To get medicine prices of different pharmacy websites & other details.
     - GET `/medicine/scrape` => To scrap names of all medicines from web and save it into an elastic index.

  - For ElasticSearch related tasks:
  
      - POST `/es/index/:indexName` => To create an index of specified type in ElasticSearch.
      - POST `/es/index/:indexName/data` => To add a document to the specified elastic index.
      
## Requests & Responses:

### 1. Search Medicine  
### Request: 

    curl --location --request GET 'localhost:8080/medicine/search?keyword=dis'
        
### Response:
```
Status code: 200 OK
 
 {
    "status": "success",
    "medicines": [
        {
            "id": "64f19bb5-4fa5-4c46-92e3-e035bbe227aa",
            "medName": "Disitch 5mg Tablet 10'S"
        },
        {
            "id": "ef116516-b6b2-4c32-b82d-2d254f8b3e1a",
            "medName": "Distinon 60mg Tablet 10'S"
        },
        {
            "id": "9bdc6d23-6fdd-4dea-b388-5501d2b65c24",
            "medName": "Dispitor 10mg Tablet 10'S"
        },
        {
            "id": "b6623f28-1454-451c-abfd-494a6a8ca312",
            "medName": "Dispitor 20mg Tablet 10'S"
        },
        {
            "id": "cb227019-c350-4d31-9036-09a23cf08a83",
            "medName": "Dispitor 40mg Tablet 10'S"
        },
        {
            "id": "a30b6bd3-ed71-4cd8-aaa5-9b76051162e9",
            "medName": "Distaclor Drops 10ml"
        },
        {
            "id": "57c31639-6628-4ff1-bb92-6d82d7a2861a",
            "medName": "Disperzyme Tablet 10'S"
        },
        {
            "id": "5fbe209d-298d-46b1-8058-cea8585eb6e9",
            "medName": "Disperzyme CD Tablet 10'S"
        },
        {
            "id": "54091f46-98f4-40a5-8156-864b35cd4e51",
            "medName": "Distaclor CD 375mg Tablet 10'S"
        },
        {
            "id": "6fcb918b-7c5a-4706-8292-b5ca769c2812",
            "medName": "Distaclor DT 250mg Tablet 6'S"
        }
    ]
}
```

### 2. Get Medicine Price:  
### Request: 

    curl --location --request GET 'localhost:8080/medicine/price?id=5fbe209d-298d-46b1-8058-cea8585eb6e9'
        
### Response:
```
Status code: 200 OK

{
    "status": "success",
    "medicine": {
        "id": "5fbe209d-298d-46b1-8058-cea8585eb6e9",
        "name": "Disperzyme CD Tablet 10'S",
        "priceList": [
            {
                "seller": {
                    "name": "netmeds.com",
                    "domain": "www.netmeds.com",
                    "logoURL": "https://www.netmeds.com/assets/gloryweb/images/home-logo-netmeds.svg"
                },
                "sellerMedName": "Disperzyme CD Tablet 10'S",
                "buyURL": "https://www.netmeds.com/prescriptions/disperzyme-cd-tablet-10-s",
                "price": 385.0,
                "qty": 1,
                "subQty": null,
                "imageURL": "https://www.netmeds.com/images/product-v1/150x150/formulation_based/tablets.jpg",
                "manufacturer": "Aksigen Pharmaceuticals Pvt Ltd",
                "lastModifiedOn": "2020-08-25T01:04:47.561"
            },
            {
                "seller": {
                    "name": "PharmEasy",
                    "domain": "pharmeasy.in",
                    "logoURL": "https://d2y2l77dht9e8d.cloudfront.net/web-assets/dist/fca22bc9.png"
                },
                "sellerMedName": "Disperzyme Cd Tablet",
                "buyURL": "https://pharmeasy.in/online-medicine-order/disperzyme-cd-tablet-1640",
                "price": 327.25,
                "qty": 1,
                "subQty": "10 Tablet(s) in Strip",
                "imageURL": "",
                "manufacturer": "MUCOS PHARMA (INDIA) PVT LTD",
                "lastModifiedOn": "2020-08-25T01:04:48.196"
            },
            {
                "seller": {
                    "name": "medplusmart",
                    "domain": "www.medplusmart.com",
                    "logoURL": null
                },
                "sellerMedName": "DISPERZYME CD TAB",
                "buyURL": "https://www.medplusmart.com/product/DISPERZYME-CD-TAB/DISP0055",
                "price": 385.0,
                "qty": 1,
                "subQty": null,
                "imageURL": null,
                "manufacturer": "aksigen hospital care",
                "lastModifiedOn": "2020-08-25T01:04:49.583"
            }
        ]
    }
}

```
### 3. Scrap Medicines  
### Request: 

    curl --location --request GET 'localhost:8080/medicine/scrape'
        
### Response:
```
  Status code: 200 OK
  
  Successfully scraped data for 23835 medicines

```

### 4. Create Index:  
### Request: 

    curl --location --request POST 'localhost:8080/es/index/med_data_index'
        
### Response:
```
  Status code: 201 Created
```

### 5. Add Document to Index:  
### Request:

```
    curl --location --request POST 'localhost:8080/es/index/med_data_index/data' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": "5fbe209d-298d-46b1-8058-cea8585eb6e9",
    "medName": "Disperzyme CD Tablet 10'\''S",
    "medNameToken0": "Disperzyme",
    "medNameToken1": "CD",
    "medNameAutoComplete": "Disperzyme CD Tablet 10'\''S"
}'
```
        
### Response:
```
  Status code: 200 OK
```
