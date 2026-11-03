package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseRegistrationServiceTest {
    @Mock StudentRepository repository;
    @Mock EmailSender emailSender;

    CourseRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new CourseRegistrationService(repository, emailSender);
    }

    @Test
    void идэвхтэйОюутан_бүртгүүлэхэдOkБуцааж_хадгалжИмэйлИлгээнэ() {
        when(repository.findStudent("S1")).thenReturn(student(true, List.of("CS101")));
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of("CS101"));

        assertEquals("OK", service.register("S1", "CS201"));

        verify(repository).saveRegistration("S1", "CS201");
        verify(emailSender).send(eq("student@example.edu"), anyString(), contains("CS201"));
    }

    @Test
    void байхгүйОюутан_бүртгүүлэхэдАлдааБуцааж_хадгалалтИмэйлХийгдэхгүй() {
        when(repository.findStudent("missing")).thenReturn(null);

        assertEquals("ERROR_NO_STUDENT", service.register("missing", "CS201"));

        verify(repository, never()).saveRegistration(anyString(), anyString());
        verifyNoInteractions(emailSender);
    }

    @Test
    void идэвхгүйОюутан_бүртгүүлэхэдАлдааБуцааж_цаашШалгахгүй() {
        when(repository.findStudent("S1")).thenReturn(student(false, List.of()));

        assertEquals("ERROR_INACTIVE_STUDENT", service.register("S1", "CS201"));

        verifyNoInteractions(emailSender);
        verify(repository, never()).courseExists(anyString());
        verify(repository, never()).saveRegistration(anyString(), anyString());
    }

    @Test
    void өмнөхХичээлДутуу_бүртгүүлэхэдАлдааБуцааж_хадгалахгүй() {
        when(repository.findStudent("S1")).thenReturn(student(true, List.of()));
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of("CS101"));

        assertEquals("ERROR_PREREQUISITES", service.register("S1", "CS201"));

        verify(repository, never()).saveRegistration(anyString(), anyString());
        verifyNoInteractions(emailSender);
    }

    @Test
    void имэйлИлгээгчАлдааШидэхэд_бүртгэлOkБайж_хадгалагдана() {
        when(repository.findStudent("S1")).thenReturn(student(true, List.of()));
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of());
        doThrow(new RuntimeException("SMTP unavailable"))
                .when(emailSender).send(anyString(), anyString(), anyString());

        assertEquals("OK", service.register("S1", "CS201"));

        verify(repository).saveRegistration("S1", "CS201");
    }

    @Test
    void nullОролт_альНэгАргументNullБол_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.register(null, "CS201"));
        assertThrows(IllegalArgumentException.class, () -> service.register("S1", null));

        verifyNoInteractions(repository, emailSender);
    }

    @Test
    void амжилттайБүртгэл_имэйлийнБиедХичээлийнКодБайна() {
        when(repository.findStudent("S1")).thenReturn(student(true, List.of()));
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of());
        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);

        assertEquals("OK", service.register("S1", "CS201"));

        verify(emailSender).send(eq("student@example.edu"), anyString(), body.capture());
        org.junit.jupiter.api.Assertions.assertTrue(body.getValue().contains("CS201"));
    }

    @ParameterizedTest
    @CsvSource({"missing,CS201,ERROR_NO_STUDENT", "inactive,CS201,ERROR_INACTIVE_STUDENT", "S1,CS404,ERROR_NO_COURSE"})
    void алдааныТөрөл_оролтоосХамаарч_зөвАлдаагБуцаана(String studentId, String courseId, String expected) {
        lenient().when(repository.findStudent("missing")).thenReturn(null);
        lenient().when(repository.findStudent("inactive")).thenReturn(student(false, List.of()));
        lenient().when(repository.findStudent("S1")).thenReturn(student(true, List.of()));
        lenient().when(repository.courseExists("CS404")).thenReturn(false);

        assertEquals(expected, service.register(studentId, courseId));
    }

    private Student student(boolean active, List<String> coursesTaken) {
        return new Student("S1", "student@example.edu", active, coursesTaken);
    }
}
