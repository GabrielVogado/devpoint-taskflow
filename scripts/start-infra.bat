#!/bin/bash
echo "Iniciando infraestrutura do TaskFlow..."
cd backend
docker-compose -f docker-compose-infra.yml up -d
echo "Infraestrutura iniciada. Verificando status..."
sleep 10
docker ps
echo "Para ver os logs de um serviço: docker logs taskflow-postgres"