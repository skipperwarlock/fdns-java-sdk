wait:
	printf 'Waiting for fdns-ms-hl7-utils\n'
	until `curl --output /dev/null --silent --head --fail http://localhost:8080`; do printf '.'; sleep 1; done
	printf 'Waiting for fdns-ms-cda-utils\n'
	until `curl --output /dev/null --silent --head --fail http://localhost:8081`; do printf '.'; sleep 1; done
	printf 'Waiting for fdns-ms-storage\n'
	until `curl --output /dev/null --silent --head --fail http://localhost:8082`; do printf '.'; sleep 1; done
	printf 'Waiting for fdns-ms-object\n'
	until `curl --output /dev/null --silent --head --fail http://localhost:8083`; do printf '.'; sleep 1; done
	printf 'Waiting for fdns-ms-indexing\n'
	until `curl --output /dev/null --silent --head --fail http://localhost:8084`; do printf '.'; sleep 1; done

up:
	docker-compose up -d
	make wait

test:
	make up
	make wait
	mvn verify
	mvn clean package
	make down

down:
	docker-compose down
