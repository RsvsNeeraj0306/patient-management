package com.pm.patient_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.pm.patient_service.kafka.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.exception.EmailAlredyExistException;
import com.pm.patient_service.exception.PatientNotFoundException;
import com.pm.patient_service.grpc.BillingServiceGrpcClient;
import com.pm.patient_service.mapper.PatientMapper;
import com.pm.patient_service.model.Patient;
import com.pm.patient_service.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private final BillingServiceGrpcClient billingServiceGrpcClient;

    @Autowired
    private final KafkaProducer kafkaProducer;


    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();
        List<PatientResponseDTO> patientResponseDTOs = patients.stream()
            .map((p-> PatientMapper.tDto(p)))
            .toList();
        return patientResponseDTOs;

    }

    public PatientResponseDTO saveNewPaitent(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlredyExistException("Patient with this email already exists");
            
        }
        Patient patient = patientRepository.save(PatientMapper.tModel(patientRequestDTO));
        // You may want to return a DTO or the saved patient, adjust as needed

        billingServiceGrpcClient.createBillingAccount(
            patient.getId().toString(), 
            patient.getName(), 
            patient.getEmail()
        );

        kafkaProducer.sendEvent(patient);
        return PatientMapper.tDto(patient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(
            () -> new PatientNotFoundException("Patient not found with id: " + id));
        
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlredyExistException("Patient with this email already exists");
        }
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate()));
        patient = patientRepository.save(patient);
        return PatientMapper.tDto(patient);
    }

    public void deletePatient(UUID id) {
        Patient patient = patientRepository.findById(id).orElseThrow(
            () -> new PatientNotFoundException("Patient not found with id: " + id));
        patientRepository.delete(patient);
    }
}
