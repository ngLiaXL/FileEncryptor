package com.ngliaxl.encrypt.cons;


public interface Cons {



    /**
     * OpenSSl工具生成密钥对
     * genrsa -out rsa_private_key.pem 1024
     * rsa -in rsa_private_key.pem -out rsa_public_key.pem -pubout
     * pkcs8 -topk8 -in rsa_private_key.pem -out pkcs8_rsa_private_key.pem -nocrypt
     */
    String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIotxyinmqcp2G6cplVuV7O7GQv0Bc0sn04OPIcXcGrknf0Q3a95ThwkGTG1OK7e7lTIAmLt5+SGaaSRBbwGYnwroI/yXHss0B/gpwsBy+tjQESrcwpFLGJzUVflNEpYm9BfSbuIWekTdSSE+IATx1/giGZW9dJyBxZF+sYNf3yxAgMBAAECgYAS8Jlm2ipZlWPELZajCR/eU9voBoexCte/JDZpse3HyRRatrAcOD1boBsP9C4T0hzK5CtnkgKCEWZzlyk5D4r6FAdd/Pz2x+m7Tgk9aev8bmLLCEN01A85AwV/NxlSzthzTE1PgT885K08lv4hfTa1uioshZcV4p2rbRDATho6EQJBANnGVr/oKDkZb3EWxiWteD4B15wPurm8N25DSqkcqwb4SoVxGhGivLPfbRrvZl/6ZRmQhCn2Kw1Zv5rzUUG2s3UCQQCibtAdI5hR//O6Ei4HMmrBotBC0Dp/jYw4bDtT9V68q8E4TPdXddKifr0/85lWF7t1Jw6cfr4damUYg7K6GajNAkB/geWhD2kdpYreiBsUTHEuvR1kvsDxpwY9hSDdy29H8XCQmRxOXx5lotfbAXjLkWxIf5kiiIPEmVvaecSF2VdRAkAO5U6cfXkoe+pj8+rYqhz0KPQkTGgw7lAflH1UU7oeXznW6ef800c/s5OzW6mCJacBNVW3sD/K/sjKmLk5K8U5AkEA1LgV4nNdsKPFH1mfcgZXKQJRp7k1IIORAQxVxPG4+s9tDSLmWUaXDLUxlJyyFOOhhcgT7ssx6PvGT56GTbLb8Q==" ;
    String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCKLccop5qnKdhunKZVblezuxkL9AXNLJ9ODjyHF3Bq5J39EN2veU4cJBkxtTiu3u5UyAJi7efkhmmkkQW8BmJ8K6CP8lx7LNAf4KcLAcvrY0BEq3MKRSxic1FX5TRKWJvQX0m7iFnpE3UkhPiAE8df4IhmVvXScgcWRfrGDX98sQIDAQAB" ;
}
