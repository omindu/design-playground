package org.hierarchical.resource.resolver;

import org.testng.annotations.Test;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class HierarchicalResourceRetrievalTest {

    @Test
    public void testFirstFoundResource() {
        HierarchicalResourceRetrievalService retrievalService = new HierarchicalResourceRetrievalService();
        Function<String, Optional<String>> retrieveFooResource = new RetrieveFooResource();
        AggregationStrategy<String, String> firstFoundStrategy = new FirstFoundAggregationStrategy<>();

        String result = retrievalService.getHierarchicalResource("subChild", retrieveFooResource, firstFoundStrategy);
        assertEquals("default_value_for_foo", result);
    }

    @Test
    public void testFirstFoundResourceWithApp() {
        HierarchicalResourceRetrievalService retrievalService = new HierarchicalResourceRetrievalService();
        BiFunction<String, String, Optional<String>> retrieveFooResourceWithApp = new RetrieveFooResourceWithApp();
        AggregationStrategy<String, String> firstFoundStrategy = new FirstFoundAggregationStrategy<>();

        String result = retrievalService.getHierarchicalResource("subChild", "subApp", retrieveFooResourceWithApp, firstFoundStrategy);
        assertEquals("default_value_for_foo_with_app", result);
    }

    @Test
    public void testAggregatedResources() {
        HierarchicalResourceRetrievalService retrievalService = new HierarchicalResourceRetrievalService();
        Function<String, Optional<String>> retrieveFooResource = new RetrieveFooResource();
        AggregationStrategy<String, List<String>> allLevelsStrategy = new AllLevelsAggregationStrategy<>();

        List<String> result = retrievalService.getHierarchicalResource("subChild", retrieveFooResource, allLevelsStrategy);
        assertTrue(result.contains("default_value_for_foo"));
    }

    @Test
    public void testAggregatedResourcesWithApp() {
        HierarchicalResourceRetrievalService retrievalService = new HierarchicalResourceRetrievalService();
        BiFunction<String, String, Optional<String>> retrieveFooResourceWithApp = new RetrieveFooResourceWithApp();
        AggregationStrategy<String, List<String>> allLevelsStrategy = new AllLevelsAggregationStrategy<>();

        List<String> result = retrievalService.getHierarchicalResource("subChild", "subApp", retrieveFooResourceWithApp, allLevelsStrategy);
        assertTrue(result.contains("default_value_for_foo_with_app"));
    }
}