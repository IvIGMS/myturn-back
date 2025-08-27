package com.ivanfrias.myturn.companies.services;

import com.ivanfrias.myturn.common.exceptions.NotFoundException;
import com.ivanfrias.myturn.companies.dao.models.entities.CompanyEntity;
import com.ivanfrias.myturn.companies.dao.repositories.CompanyRepository;
import com.ivanfrias.myturn.model.CompanyDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ModelMapper modelMapper;

    private CompanyEntity companyEntity;

    @BeforeEach
    void setUp() {
        companyEntity = new CompanyEntity();
        companyEntity.setId(1L);
        companyEntity.setName("Test Corp");
    }

    @Nested
    @DisplayName("FindCompanyEntityByOwnerId Tests")
    class FindCompanyEntityByOwnerIdTests {
        @Test
        @DisplayName("Should return CompanyEntity when company exists")
        void findCompanyEntityByOwnerId_whenCompanyExists_shouldReturnCompanyEntity() {
            when(companyRepository.findByOwner_id(1L)).thenReturn(Optional.of(companyEntity));

            CompanyEntity result = companyService.findCompanyEntityByOwnerId(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should return null when company does not exist")
        void findCompanyEntityByOwnerId_whenCompanyDoesNotExist_shouldReturnNull() {
            when(companyRepository.findByOwner_id(1L)).thenReturn(Optional.empty());

            CompanyEntity result = companyService.findCompanyEntityByOwnerId(1L);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("FindCompanyByOwnerId Tests")
    class FindCompanyByOwnerIdTests {
        @Test
        @DisplayName("Should return CompanyDTO when company exists")
        void findCompanyByOwnerId_whenCompanyExists_shouldReturnCompanyDTO() {
            CompanyDTO companyDTO = new CompanyDTO();
            companyDTO.setId(1L);

            when(companyRepository.findByOwner_id(1L)).thenReturn(Optional.of(companyEntity));
            when(modelMapper.map(companyEntity, CompanyDTO.class)).thenReturn(companyDTO);

            CompanyDTO result = companyService.findCompanyByOwnerId(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }
    }

    @Nested
    @DisplayName("GetCompanyById Tests")
    class GetCompanyByIdTests {
        @Test
        @DisplayName("Should return CompanyDTO when company exists")
        void getCompanyById_whenCompanyExists_shouldReturnCompanyDTO() {
            CompanyDTO companyDTO = new CompanyDTO();
            companyDTO.setId(1L);

            when(companyRepository.findById(1L)).thenReturn(Optional.of(companyEntity));
            when(modelMapper.map(companyEntity, CompanyDTO.class)).thenReturn(companyDTO);

            CompanyDTO result = companyService.getCompanyById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should throw NotFoundException when company does not exist")
        void getCompanyById_whenCompanyDoesNotExist_shouldThrowNotFoundException() {
            when(companyRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> companyService.getCompanyById(1L));
        }
    }

    @Nested
    @DisplayName("Save Tests")
    class SaveTests {
        @Test
        @DisplayName("Should return saved CompanyEntity")
        void save_shouldReturnSavedCompanyEntity() {
            when(companyRepository.save(any(CompanyEntity.class))).thenReturn(companyEntity);

            CompanyEntity result = companyService.save(new CompanyEntity());

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }
    }

    @Nested
    @DisplayName("GetCompanyByLinkedCode Tests")
    class GetCompanyByLinkedCodeTests {
        @Test
        @DisplayName("Should return CompanyEntity when company exists")
        void getCompanyByLinkedCode_whenCompanyExists_shouldReturnCompanyEntity() {
            when(companyRepository.findByLinkCode("link-code")).thenReturn(Optional.of(companyEntity));

            CompanyEntity result = companyService.getCompanyByLinkedCode("link-code");

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should throw NotFoundException when company does not exist")
        void getCompanyByLinkedCode_whenCompanyDoesNotExist_shouldThrowNotFoundException() {
            when(companyRepository.findByLinkCode("link-code")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> companyService.getCompanyByLinkedCode("link-code"));
        }
    }

    @Nested
    @DisplayName("FindAll Tests")
    class FindAllTests {
        @Test
        @DisplayName("Should return a list of companies when companies exist")
        void findAll_whenCompaniesExist_shouldReturnCompanyList() {
            when(companyRepository.findAll()).thenReturn(Collections.singletonList(companyEntity));

            List<CompanyEntity> result = companyService.findAll();

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should return an empty list when no companies exist")
        void findAll_whenNoCompaniesExist_shouldReturnEmptyList() {
            when(companyRepository.findAll()).thenReturn(Collections.emptyList());

            List<CompanyEntity> result = companyService.findAll();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
