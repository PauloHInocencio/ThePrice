package br.com.noartcode.theprice.data.remote.networking

object MockedApiResponses {
    const val GET_BILLS_MOCK_RESPONSE = """
        [
            {
                "id": "7b98a36f-2694-429d-897f-cc61ca22eccb",
                "user_id": "c5dd57b9-aadd-417d-b784-04b394cc434c",
                "name": "Electricity Bill",
                "description": "Monthly payment for electricity usage in your home.",
                "price": 9454,
                "type": "MONTHLY",
                "billing_start_date": "2025-01-05T00:00:00Z",
                "created_at": "2025-01-14T10:00:00Z",
                "updated_at": "2025-01-17T10:00:00Z"
            },
            {
                "id": "9d94ae70-7d60-422e-bd86-eace3b713a04",
                "user_id": "c5dd57b9-aadd-417d-b784-04b394cc434c",
                "name": "Health Insurance",
                "description": "Monthly premium for your health insurance.",
                "price": 20454,
                "type": "MONTHLY",
                "billing_start_date": "2025-01-05T00:00:00Z",
                "created_at": "2025-01-14T00:00:00Z",
                "updated_at": "2025-01-17T00:00:00Z"
            },
            {
                "id": "5dfe3b88-ea9e-4f04-9cd3-9eac88d0bce2",
                "user_id": "c5dd57b9-aadd-417d-b784-04b394cc434c",
                "name": "Apartment Rent",
                "description": "Monthly rent payment for your apartment.",
                "price": 25454,
                "type": "MONTHLY",
                "billing_start_date": "2025-01-05T00:00:00Z",
                "created_at": "2025-01-14T00:00:00Z",
                "updated_at": "2025-01-17T00:00:00Z"
            }
        ]
    """

    const val GET_PAYMENTS_MOCK_RESPONSE = """
    [
          {
            "id": "7c4c625d-b349-48bb-b997-5c4329e5db27",
            "bill_id": "7b98a36f-2694-429d-897f-cc61ca22eccb",
            "is_payed": false,
            "price": 9454,
            "due_date": "2025-01-05T00:00:00Z",
            "created_at": "2025-03-06T07:22:58.743302Z",
            "updated_at": "2025-03-06T07:22:58.743311Z"
          },
          {
            "id": "64165f16-d4ea-46e0-9247-080f940ff28c",
            "bill_id": "7b98a36f-2694-429d-897f-cc61ca22eccb",
            "is_payed": false,
            "price": 9454,
            "due_date": "2025-02-04T00:00:00Z",
            "created_at": "2025-03-06T07:22:58.743339Z",
            "updated_at": "2025-03-06T07:22:58.743344Z"
          },
          {
            "id": "ef8e1966-3af1-40f6-9503-a8bcff659723",
            "bill_id": "7b98a36f-2694-429d-897f-cc61ca22eccb",
            "is_payed": false,
            "price": 9454,
            "due_date": "2025-03-06T00:00:00Z",
            "created_at": "2025-03-06T07:22:58.743416Z",
            "updated_at": "2025-03-06T07:22:58.743424Z"
          },
          {
            "id": "d5360f53-9e5d-4f7d-a233-8047a4beea3d",
            "bill_id": "9d94ae70-7d60-422e-bd86-eace3b713a04",
            "is_payed": false,
            "price": 20454,
            "due_date": "2025-01-05T00:00:00Z",
            "created_at": "2025-03-06T07:22:58.743491Z",
            "updated_at": "2025-03-06T07:22:58.743498Z"
          },
          {
            "id": "4711856e-5608-415e-b0fd-f34f8ab30ccd",
            "bill_id": "9d94ae70-7d60-422e-bd86-eace3b713a04",
            "is_payed": false,
            "price": 20454,
            "due_date": "2025-02-05T00:00:00Z",
            "created_at": "2025-03-06T07:22:58.743826Z",
            "updated_at": "2025-03-06T07:22:58.743833Z"
          },
          {
            "id": "be29cb9d-3bfd-44f9-ac71-e31071f629ee",
            "bill_id": "9d94ae70-7d60-422e-bd86-eace3b713a04",
            "is_payed": false,
            "price": 20454,
            "due_date": "2025-03-05T00:00:00Z",
            "created_at": "2025-03-06T07:22:58.743871Z",
            "updated_at": "2025-03-06T07:22:58.743874Z"
          },
          {
            "id": "21743a0b-6f46-4157-8d3d-64f37cc93a59",
            "bill_id": "5dfe3b88-ea9e-4f04-9cd3-9eac88d0bce2",
            "is_payed": false,
            "price": 25454,
            "due_date": "2025-01-05T00:00:00Z",
            "created_at": "2025-03-05T07:22:58.743921Z",
            "updated_at": "2025-03-06T07:22:58.743925Z"
          },
          {
            "id": "04511d1d-4dbc-4830-8406-e60bb72e131a",
            "bill_id": "5dfe3b88-ea9e-4f04-9cd3-9eac88d0bce2",
            "is_payed": false,
            "price": 25454,
            "due_date": "2025-02-04T00:00:00Z",
            "created_at": "2025-03-05T07:22:58.743958Z",
            "updated_at": "2025-03-06T07:22:58.743961Z"
          },
          {
            "id": "1c1550f7-43d3-4889-80c2-46c3ee9b5265",
            "bill_id": "5dfe3b88-ea9e-4f04-9cd3-9eac88d0bce2",
            "is_payed": false,
            "price": 25454,
            "due_date": "2025-03-05T00:00:00Z",
            "created_at": "2025-03-06T07:22:58.743983Z",
            "updated_at": "2025-03-06T07:22:58.743986Z"
          }
    ]
    """


    fun userCreationMockResponse(name: String, email: String) = """
    {
        "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY1MTdmNjM1LWZkMjQtNDU3Mi04YjI1LTNhZTNhZGUzZDI0OCIsImRldmljZV9pZCI6IjFjMTU1MGY3LTQzZDMtNDg4OS04MGMyLTQ2YzNlZTliNTI2NSIsImV4cCI6MTc1OTc1MzUyNywiaWF0IjoxNzU5NzUyNjI3LCJqdGkiOiJlNWVhZTIwMC0xZjYyLTQwNjEtYmIxOC1iNTMxNWEyNTAyNzUifQ.uLTFtgEkZ7kw68uHk2Pl4vLm_HYBodzsoUsf_skikwE",
        "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY1MTdmNjM1LWZkMjQtNDU3Mi04YjI1LTNhZTNhZGUzZDI0OCIsImRldmljZV9pZCI6IjFjMTU1MGY3LTQzZDMtNDg4OS04MGMyLTQ2YzNlZTliNTI2NSIsImV4cCI6MTc2NzUyODYyNywiaWF0IjoxNzU5NzUyNjI3LCJqdGkiOiJiN2MxNzdiNS04YTI5LTQxNDAtYWNmNi02MjRlNGQwMTY3NDUifQ.E2JmSLUFHLdmsRjn8zIp_W-92YXiBALVFy0a1O8fMXM",
        "name": "$name",
        "email": "$email",
        "picture": ""
    }    
    """
}

