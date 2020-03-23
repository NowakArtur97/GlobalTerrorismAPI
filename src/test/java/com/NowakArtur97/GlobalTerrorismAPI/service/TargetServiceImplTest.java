package com.NowakArtur97.GlobalTerrorismAPI.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.NowakArtur97.GlobalTerrorismAPI.node.Target;
import com.NowakArtur97.GlobalTerrorismAPI.repository.TargetRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Target Service Impl Tests")
@Tag("TargetServiceImpl_Tests")
public class TargetServiceImplTest {

	private TargetService targetService;

	@Mock
	private TargetRepository targetRepository;

	@BeforeEach
	void setUp() {

		targetService = new TargetServiceImpl(targetRepository);
	}

	@Test
	@DisplayName("when targets exist and return all targets")
	public void when_targets_exist_and_return_all_targets_should_return_targets() {

		List<Target> targetsExpected = new ArrayList<>();

		Target target1 = new Target("target1");
		Target target2 = new Target("target2");
		Target target3 = new Target("target3");

		targetsExpected.add(target1);
		targetsExpected.add(target2);
		targetsExpected.add(target3);

		when(targetRepository.findAll()).thenReturn(targetsExpected);

		List<Target> targetsActual = targetService.findAll();

		assertAll(() -> assertNotNull(targetsActual, () -> "shouldn`t return null"),
				() -> assertTrue(targetsActual.contains(target1),
						() -> "should contain: " + target1 + ", but was: " + targetsActual),
				() -> assertTrue(targetsActual.contains(target2),
						() -> "should contain: " + target2 + ", but was: " + targetsActual),
				() -> assertTrue(targetsActual.contains(target3),
						() -> "should contain: " + target3 + ", but was: " + targetsActual),
				() -> assertEquals(targetsExpected.size(), targetsActual.size(), () -> "should return list with: "
						+ targetsExpected.size() + " elements, but was: " + targetsActual.size()));
	}
}
