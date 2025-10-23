package com.stripeflow.service;

import com.stripeflow.dto.ChargeResponse;
import com.stripeflow.dto.CreateChargeRequest;
import com.stripeflow.model.Charge;
import com.stripeflow.model.Customer;
import com.stripeflow.repository.ChargeRepository;
import com.stripeflow.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ChargeService
 */
@ExtendWith(MockitoExtension.class)
class ChargeServiceTest {
    
    @Mock
    private ChargeRepository chargeRepository;
    
    @Mock
    private CustomerRepository customerRepository;
    
    @InjectMocks
    private ChargeService chargeService;
    
    private Customer testCustomer;
    private Charge testCharge;
    private CreateChargeRequest testRequest;
    
    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setEmail("test@example.com");
        testCustomer.setName("Test Customer");
        
        testCharge = new Charge();
        testCharge.setId(1L);
        testCharge.setAmount(new BigDecimal("100.00"));
        testCharge.setCurrency("USD");
        testCharge.setCustomer(testCustomer);
        testCharge.setStatus(Charge.ChargeStatus.SUCCEEDED);
        testCharge.setDescription("Test charge");
        
        testRequest = new CreateChargeRequest();
        testRequest.setAmount(new BigDecimal("100.00"));
        testRequest.setCurrency("USD");
        testRequest.setCustomerId(1L);
        testRequest.setDescription("Test charge");
    }
    
    @Test
    void createCharge_Success() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(chargeRepository.existsByIdempotencyKey(anyString())).thenReturn(false);
        when(chargeRepository.save(any(Charge.class))).thenReturn(testCharge);
        
        // When
        ChargeResponse result = chargeService.createCharge(testRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(testCharge.getId(), result.getId());
        assertEquals(testCharge.getAmount(), result.getAmount());
        assertEquals(testCharge.getCurrency(), result.getCurrency());
        verify(customerRepository).findById(1L);
        verify(chargeRepository).save(any(Charge.class));
    }
    
    @Test
    void createCharge_CustomerNotFound_ThrowsException() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> chargeService.createCharge(testRequest));
        
        assertEquals("Customer not found with ID: 1", exception.getMessage());
        verify(customerRepository).findById(1L);
        verify(chargeRepository, never()).save(any(Charge.class));
    }
    
    @Test
    void createCharge_InvalidAmount_ThrowsException() {
        // Given
        testRequest.setAmount(BigDecimal.ZERO);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> chargeService.createCharge(testRequest));
        
        assertEquals("Charge amount must be greater than zero", exception.getMessage());
        verify(customerRepository).findById(1L);
        verify(chargeRepository, never()).save(any(Charge.class));
    }
    
    @Test
    void createCharge_IdempotencyKeyExists_ReturnsExistingCharge() {
        // Given
        testRequest.setIdempotencyKey("test-key");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(chargeRepository.existsByIdempotencyKey("test-key")).thenReturn(true);
        when(chargeRepository.findByIdempotencyKey("test-key")).thenReturn(Optional.of(testCharge));
        
        // When
        ChargeResponse result = chargeService.createCharge(testRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(testCharge.getId(), result.getId());
        verify(chargeRepository).existsByIdempotencyKey("test-key");
        verify(chargeRepository).findByIdempotencyKey("test-key");
        verify(chargeRepository, never()).save(any(Charge.class));
    }
    
    @Test
    void getChargeById_Success() {
        // Given
        when(chargeRepository.findById(1L)).thenReturn(Optional.of(testCharge));
        
        // When
        ChargeResponse result = chargeService.getChargeById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testCharge.getId(), result.getId());
        assertEquals(testCharge.getAmount(), result.getAmount());
        verify(chargeRepository).findById(1L);
    }
    
    @Test
    void getChargeById_NotFound_ThrowsException() {
        // Given
        when(chargeRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> chargeService.getChargeById(1L));
        
        assertEquals("Charge not found with ID: 1", exception.getMessage());
        verify(chargeRepository).findById(1L);
    }
    
    @Test
    void getAllCharges_Success() {
        // Given
        List<Charge> charges = Arrays.asList(testCharge);
        Page<Charge> chargePage = new PageImpl<>(charges);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(chargeRepository.findAll(pageable)).thenReturn(chargePage);
        
        // When
        Page<ChargeResponse> result = chargeService.getAllCharges(pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testCharge.getId(), result.getContent().get(0).getId());
        verify(chargeRepository).findAll(pageable);
    }
    
    @Test
    void getChargesByCustomer_Success() {
        // Given
        List<Charge> charges = Arrays.asList(testCharge);
        Page<Charge> chargePage = new PageImpl<>(charges);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(chargeRepository.findByCustomer(testCustomer, pageable)).thenReturn(chargePage);
        
        // When
        Page<ChargeResponse> result = chargeService.getChargesByCustomer(1L, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testCharge.getId(), result.getContent().get(0).getId());
        verify(customerRepository).findById(1L);
        verify(chargeRepository).findByCustomer(testCustomer, pageable);
    }
    
    @Test
    void getChargesByCustomer_CustomerNotFound_ThrowsException() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        Pageable pageable = PageRequest.of(0, 10);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> chargeService.getChargesByCustomer(1L, pageable));
        
        assertEquals("Customer not found with ID: 1", exception.getMessage());
        verify(customerRepository).findById(1L);
        verify(chargeRepository, never()).findByCustomer(any(), any());
    }
    
    @Test
    void getChargesByStatus_Success() {
        // Given
        List<Charge> charges = Arrays.asList(testCharge);
        Page<Charge> chargePage = new PageImpl<>(charges);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(chargeRepository.findByStatus(Charge.ChargeStatus.SUCCEEDED, pageable)).thenReturn(chargePage);
        
        // When
        Page<ChargeResponse> result = chargeService.getChargesByStatus(Charge.ChargeStatus.SUCCEEDED, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testCharge.getId(), result.getContent().get(0).getId());
        verify(chargeRepository).findByStatus(Charge.ChargeStatus.SUCCEEDED, pageable);
    }
    
    @Test
    void getChargesByCurrency_Success() {
        // Given
        List<Charge> charges = Arrays.asList(testCharge);
        Page<Charge> chargePage = new PageImpl<>(charges);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(chargeRepository.findByCurrency("USD", pageable)).thenReturn(chargePage);
        
        // When
        Page<ChargeResponse> result = chargeService.getChargesByCurrency("USD", pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testCharge.getId(), result.getContent().get(0).getId());
        verify(chargeRepository).findByCurrency("USD", pageable);
    }
    
    @Test
    void updateChargeStatus_Success() {
        // Given
        when(chargeRepository.findById(1L)).thenReturn(Optional.of(testCharge));
        when(chargeRepository.save(any(Charge.class))).thenReturn(testCharge);
        
        // When
        ChargeResponse result = chargeService.updateChargeStatus(1L, Charge.ChargeStatus.FAILED);
        
        // Then
        assertNotNull(result);
        assertEquals(testCharge.getId(), result.getId());
        verify(chargeRepository).findById(1L);
        verify(chargeRepository).save(any(Charge.class));
    }
    
    @Test
    void cancelCharge_Success() {
        // Given
        testCharge.setStatus(Charge.ChargeStatus.PENDING);
        when(chargeRepository.findById(1L)).thenReturn(Optional.of(testCharge));
        when(chargeRepository.save(any(Charge.class))).thenReturn(testCharge);
        
        // When
        ChargeResponse result = chargeService.cancelCharge(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testCharge.getId(), result.getId());
        verify(chargeRepository).findById(1L);
        verify(chargeRepository).save(any(Charge.class));
    }
    
    @Test
    void cancelCharge_InvalidStatus_ThrowsException() {
        // Given
        testCharge.setStatus(Charge.ChargeStatus.SUCCEEDED);
        when(chargeRepository.findById(1L)).thenReturn(Optional.of(testCharge));
        
        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> chargeService.cancelCharge(1L));
        
        assertEquals("Cannot cancel charge with status: SUCCEEDED", exception.getMessage());
        verify(chargeRepository).findById(1L);
        verify(chargeRepository, never()).save(any(Charge.class));
    }
    
    @Test
    void getChargeStatistics_Success() {
        // Given
        when(chargeRepository.count()).thenReturn(100L);
        when(chargeRepository.countChargesByStatus(Charge.ChargeStatus.SUCCEEDED)).thenReturn(90L);
        when(chargeRepository.countChargesByStatus(Charge.ChargeStatus.FAILED)).thenReturn(10L);
        
        // When
        ChargeService.ChargeStatistics result = chargeService.getChargeStatistics();
        
        // Then
        assertNotNull(result);
        assertEquals(100L, result.getTotalCharges());
        assertEquals(90L, result.getSuccessfulCharges());
        assertEquals(10L, result.getFailedCharges());
        assertEquals(90.0, result.getSuccessRate());
        verify(chargeRepository).count();
        verify(chargeRepository).countChargesByStatus(Charge.ChargeStatus.SUCCEEDED);
        verify(chargeRepository).countChargesByStatus(Charge.ChargeStatus.FAILED);
    }
}



