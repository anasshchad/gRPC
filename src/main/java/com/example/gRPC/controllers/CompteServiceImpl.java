package com.example.gRPC.controllers;

import com.example.gRPC.services.CompteService;
import ma.project.grpc.stubs.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.stream.Collectors;

@GrpcService
public class CompteServiceImpl extends CompteServiceGrpc.CompteServiceImplBase {

    private final CompteService compteService;

    public CompteServiceImpl(CompteService compteService) {
        this.compteService = compteService;
    }

    /**
     * Conversion de l'entité JPA vers le message gRPC
     */
    private Compte convertCompteEntityToGrpc(com.example.gRPC.entities.Compte entity) {
        return Compte.newBuilder()
                .setId(entity.getId())
                .setSolde(entity.getSolde())
                .setDateCreation(entity.getDateCreation())
                .setType(TypeCompte.valueOf(entity.getType()))
                .build();
    }

    /**
     * RPC: Récupérer tous les comptes
     */
    @Override
    public void allComptes(GetAllComptesRequest request, StreamObserver<GetAllComptesResponse> responseObserver) {
        var comptes = compteService.findAllComptes().stream()
                .map(this::convertCompteEntityToGrpc)
                .collect(Collectors.toList());

        responseObserver.onNext(GetAllComptesResponse.newBuilder()
                .addAllComptes(comptes)
                .build());
        responseObserver.onCompleted();
    }

    /**
     * RPC: Récupérer un compte par son ID
     */
    @Override
    public void compteById(GetCompteByIdRequest request, StreamObserver<GetCompteByIdResponse> responseObserver) {
        com.example.gRPC.entities.Compte entity = compteService.findCompteById(request.getId());

        if (entity != null) {
            Compte grpcCompte = convertCompteEntityToGrpc(entity);
            responseObserver.onNext(GetCompteByIdResponse.newBuilder()
                    .setCompte(grpcCompte)
                    .build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Compte non trouvé avec l'ID: " + request.getId())
                    .asRuntimeException());
        }
    }

    /**
     * RPC: Sauvegarder un nouveau compte
     */
    @Override
    public void saveCompte(SaveCompteRequest request, StreamObserver<SaveCompteResponse> responseObserver) {
        var compteReq = request.getCompte();

        // Création de l'entité JPA
        var compteEntity = new com.example.gRPC.entities.Compte();
        compteEntity.setSolde(compteReq.getSolde());
        compteEntity.setDateCreation(compteReq.getDateCreation());
        compteEntity.setType(compteReq.getType().name());

        // Sauvegarde dans la base de données
        var savedCompte = compteService.saveCompte(compteEntity);

        // Conversion en message gRPC
        var grpcCompte = convertCompteEntityToGrpc(savedCompte);

        responseObserver.onNext(SaveCompteResponse.newBuilder()
                .setCompte(grpcCompte)
                .build());
        responseObserver.onCompleted();
    }
}