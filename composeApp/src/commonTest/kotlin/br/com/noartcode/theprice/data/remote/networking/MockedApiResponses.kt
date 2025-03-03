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
                "type": "Utility",
                "billing_start_date": "2025-01-20T00:00:00Z",
                "created_at": "2025-01-14T10:00:00Z",
                "updated_at": "2025-01-17T10:00:00Z"
            },
            {
                "id": "9d94ae70-7d60-422e-bd86-eace3b713a04",
                "user_id": "c5dd57b9-aadd-417d-b784-04b394cc434c",
                "name": "Netflix Subscription",
                "description": "Monthly payment for Netflix streaming service.",
                "price": 10454,
                "type": "Subscription",
                "billing_start_date": "2025-02-19T00:00:00Z",
                "created_at": "2025-01-14T00:00:00Z",
                "updated_at": "2025-01-17T00:00:00Z"
            },
            {
                "id": "5dfe3b88-ea9e-4f04-9cd3-9eac88d0bce2",
                "user_id": "c5dd57b9-aadd-417d-b784-04b394cc434c",
                "name": "Car Loan",
                "description": "Monthly installment for your car loan.",
                "price": 15454,
                "type": "Loan",
                "billing_start_date": "2025-03-21T00:00:00Z",
                "created_at": "2025-01-14T00:00:00Z",
                "updated_at": "2025-01-17T00:00:00Z"
            },
            {
                "id": "9b028639-f854-4360-aeb2-d1ab98e4d71b",
                "user_id": "c5dd57b9-aadd-417d-b784-04b394cc434c",
                "name": "Health Insurance",
                "description": "Monthly premium for your health insurance.",
                "price": 20454,
                "type": "Insurance",
                "billing_start_date": "2025-04-20T00:00:00Z",
                "created_at": "2025-01-14T00:00:00Z",
                "updated_at": "2025-01-17T00:00:00Z"
            },
            {
                "id": "9ee7061d-4971-48b3-911c-39aaf1574805",
                "user_id": "c5dd57b9-aadd-417d-b784-04b394cc434c",
                "name": "Apartment Rent",
                "description": "Monthly rent payment for your apartment.",
                "price": 25454,
                "type": "Rent",
                "billing_start_date": "2025-05-20T00:00:00Z",
                "created_at": "2025-01-14T00:00:00Z",
                "updated_at": "2025-01-17T00:00:00Z"
            }
        ]
    """
}

