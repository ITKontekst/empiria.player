package eu.ydp.empiria.player.client.module.expression.adapters;

import static eu.ydp.empiria.player.client.module.expression.adapters.ExpressionAdapterReplacementsProvider.SELECTOR;
import static eu.ydp.empiria.player.client.resources.EmpiriaStyleNameConstants.EMPIRIA_EXPRESSION_REPLACEMENTS;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;

import eu.ydp.empiria.player.client.style.StyleSocket;

public class ExpressionAdapterReplacementsProviderJUnitTest {

	private ExpressionAdapterReplacementsProvider provider;
	private final StyleSocket styleSocket = mock(StyleSocket.class);
	private final ExpressionAdapterReplacementsParser parser = Guice.createInjector().getInstance(ExpressionAdapterReplacementsParser.class);
	
	@Before
	public void setUp() {
		provider = new ExpressionAdapterReplacementsProvider(styleSocket, parser);
	}
	
	@Test
	public void getReplacements(){
		// given
		when(styleSocket.getStyles(SELECTOR)).thenReturn(ImmutableMap.of(EMPIRIA_EXPRESSION_REPLACEMENTS, " ×|*|:,;,÷|/|≤|<= "));
		
		// when
		Map<String, String> replacements = provider.getReplacements();
		
		// then
		assertThat(replacements).isEqualTo(ImmutableMap.of("×","*",":","/",";","/","÷","/","≤","<="));
	}
	
	@Test
	public void caching(){
		// given
		when(styleSocket.getStyles(SELECTOR)).thenReturn(ImmutableMap.of(EMPIRIA_EXPRESSION_REPLACEMENTS, " ×,•|*|:,;|/ "));
		
		// when
		provider.getReplacements();
		provider.getReplacements();
		
		// then
		verify(styleSocket).getStyles(SELECTOR);
	}
	
	@Test
	public void replacementsNotSet(){
		// given
		when(styleSocket.getStyles(SELECTOR)).thenReturn(new HashMap<String, String>());
		
		// when
		Map<String, String> replacements = provider.getReplacements();
		
		// then
		assertThat(replacements).isEmpty();
	}
	
	@Test
	public void empty(){
		// given
		when(styleSocket.getStyles(SELECTOR)).thenReturn(ImmutableMap.of(EMPIRIA_EXPRESSION_REPLACEMENTS, ""));
		
		// when
		Map<String, String> replacements = provider.getReplacements();
		
		// then
		assertThat(replacements).isEmpty();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidNumberOfParts(){
		// given
		when(styleSocket.getStyles(SELECTOR)).thenReturn(ImmutableMap.of(EMPIRIA_EXPRESSION_REPLACEMENTS, " *|+|/"));
		
		// when
		provider.getReplacements();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidMapping(){
		// given
		when(styleSocket.getStyles(SELECTOR)).thenReturn(ImmutableMap.of(EMPIRIA_EXPRESSION_REPLACEMENTS, " *||:|/"));
		
		// when
		provider.getReplacements();
	}
	
}
