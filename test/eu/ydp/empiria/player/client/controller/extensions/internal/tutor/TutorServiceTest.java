package eu.ydp.empiria.player.client.controller.extensions.internal.tutor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;
import eu.ydp.empiria.player.client.gin.factory.PersonaServiceFactory;

@RunWith(MockitoJUnitRunner.class)
public class TutorServiceTest {
	@Mock
	private PersonaServiceFactory personaServiceFactory;
	@InjectMocks
	private TutorService tutorService;
	private TutorConfig tutorConfig;

	@Test
	public void shouldReturnRegisteredTutorConfig() throws Exception {
		//given
		String tutorId = "tutorId";
		tutorConfig = mock(TutorConfig.class);
		tutorService.registerTutor(tutorId, tutorConfig);
		
		//when
		TutorConfig result = tutorService.getTutorConfig(tutorId);
		
		//then
		assertThat(result).isEqualTo(tutorConfig);
	}
	
	@Test
	public void shouldCreateNewPersonaService() throws Exception {
		//given
		String tutorId = "tutorId";
		PersonaService createdPersonaService = expectPersonaServiceCreationForTutorId(tutorId);
		
		//when
		PersonaService result = tutorService.getTutorPersonaService(tutorId);
		
		//then
		assertThat(result).isEqualTo(createdPersonaService);
	}

	
	@Test
	public void shouldReturnPreviouslyCreatedPersonaService() throws Exception {
		//given
		String tutorId = "tutorId";
		PersonaService createdPersonaService = expectPersonaServiceCreationForTutorId(tutorId);
		
		//when
		tutorService.getTutorPersonaService(tutorId);
		PersonaService personaService = tutorService.getTutorPersonaService(tutorId);
		
		//then
		assertThat(personaService).isEqualTo(createdPersonaService);
		verify(personaServiceFactory, times(1)).createPersonaService(tutorConfig);
	}
	
	@Test
	public void shouldReturnDifferentServicesForDifferentTutors() throws Exception {
		//given
		String tutorId1 = "tutorId1";
		PersonaService createdPersonaService1 = expectPersonaServiceCreationForTutorId(tutorId1);
		
		String tutorId2 = "tutorId2";
		PersonaService createdPersonaService2 = expectPersonaServiceCreationForTutorId(tutorId2);
		
		//when
		tutorService.getTutorPersonaService(tutorId1);
		PersonaService personaService1 = tutorService.getTutorPersonaService(tutorId1);

		tutorService.getTutorPersonaService(tutorId2);
		PersonaService personaService2 = tutorService.getTutorPersonaService(tutorId2);
		
		
		assertThat(personaService1).isEqualTo(createdPersonaService1);
		assertThat(personaService2).isEqualTo(createdPersonaService2);
		assertThat(personaService1).isNotEqualTo(personaService2);
	}
	
	private PersonaService expectPersonaServiceCreationForTutorId(String tutorId) {
		tutorConfig = mock(TutorConfig.class);
		PersonaService createdPersonaService = mock(PersonaService.class);
		
		tutorService.registerTutor(tutorId, tutorConfig);
		
		when(personaServiceFactory.createPersonaService(tutorConfig))
			.thenReturn(createdPersonaService);
		
		return createdPersonaService;
	}
}
