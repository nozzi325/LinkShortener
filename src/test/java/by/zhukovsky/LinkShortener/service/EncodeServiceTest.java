package by.zhukovsky.LinkShortener.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EncodeServiceTest {
    @InjectMocks EncodeService subj;

    @Test
    void generateRandomUrl() {
        var result = subj.generateRandomUrl();
        assertEquals(7, result.length());
    }
}