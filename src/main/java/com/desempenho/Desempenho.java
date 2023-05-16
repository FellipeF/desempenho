package com.desempenho;

/*
Autor: Fellipe Freire de Oliveira
Universidade Federal do Vale do São Francisco
Engenharia da Computação
*/

import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import org.bson.conversions.Bson;


public class Desempenho {

    public static void main(String[] args) {
                    
        int BI = 500;   //Blocos de Insert
        int BS = 500;   //Blocos de Select
        int BU = 500;   //Blocos de Update
        
        double dt;
        int R = 10;     //Quantidade de Repetições
        
        //Variáveis Sentinela
        int i;
        int r;
        
        String uri = "mongodb+srv://user:chave"; //Conexão com o Banco de Dados utilizando uma chave.
        
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("filmes");
            
            
            System.out.println("INSERT: ");
            System.out.println("");
            
            //-------------------------- INSERT --------------------------------
            for (r = 0; r < R; r++)
            {
                Instant inicio_insercao = Instant.now();
            
                for (i = 0; i < BI; i++)
                {
                    database.getCollection("informacoes").insertMany(Arrays.asList(new Document("Título","Star Wars IV - Uma Nova Esperança")
                        .append("Gênero", "Ficção Científica")
                        .append("Elenco","Mark Hamill, Harrison Ford, Carrie Fisher" )
                        .append("Diretor", "George Lucas")
                        .append("Duração", "02h01min")
                        .append("Primary Key", i + 1)));
                }
                Instant fim_insercao = Instant.now();
                Duration tempo_gasto_insercao = Duration.between(inicio_insercao,fim_insercao);
                dt = tempo_gasto_insercao.toNanos()/(Math.pow(10, 9) * BI);
                System.out.printf("Tempo Gasto: %.5f segundos\n", dt);
            }
            
            System.out.println("-------------------------------------------");
            System.out.println("");
            System.out.println("SELECT");
            System.out.println("");
            
            //-------------------------- SELECT --------------------------------
            
            for (r = 0; r < R; r++)
            {
                Instant inicio_select = Instant.now();
                
                for (i = 0; i < BS; i ++)
                {
                    Bson filter = Filters.and(Filters.gt("Diretor","George Lucas"), Filters.lt("Duração", "02h01min"));
                    database.getCollection("informacoes").find(filter).forEach(doc -> System.out.println(doc.toJson()));
                }
                Instant fim_select = Instant.now();
                Duration tempo_gasto_select = Duration.between(inicio_select,fim_select);
                dt = tempo_gasto_select.toNanos()/(Math.pow(10, 9) * BI);
                System.out.printf("Tempo Gasto: %.5f segundos\n", dt);
            }
            
            System.out.println("-------------------------------------------");
            System.out.println("");
            System.out.println("UPDATE");
            System.out.println("");
            
            //-------------------------- UPDATE --------------------------------
            
            for (r = 0; r < R; r++)
            {
                Instant inicio_update = Instant.now();
                
                for (i = 0; i < BU; i++)
                {
                    Bson filter = new Document("Duração", "02h01min");
                    Bson newValue = new Document("Duração","02h12min");
                    Bson updateOperationDocument = new Document("$set",newValue);
                    database.getCollection("informacoes").updateMany(filter,updateOperationDocument);
                }
                Instant fim_update = Instant.now();
                Duration tempo_gasto_update = Duration.between(inicio_update,fim_update);
                dt = tempo_gasto_update.toNanos()/(Math.pow(10, 9) * BI);
                System.out.printf("Tempo Gasto: %.5f segundos\n", dt);
            }
        }
    }
}
