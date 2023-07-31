kafka-avro-console-producer \
  --broker-list kafka:9092 --topic topic1 \
  --property value.schema='{
	"type": "record",
	"name": "MyRecord",
	"namespace": "com.example.kafkaconsumer.models.avro",
	"doc": "This is a sample Avro schema to get you started. Please edit",
	"fields": [
		{
			"name": "name",
			"type": "string"
		}
	]
}'