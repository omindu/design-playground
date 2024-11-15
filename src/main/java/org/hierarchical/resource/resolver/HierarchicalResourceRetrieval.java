package org.hierarchical.resource.resolver;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Demonstrates the retrieval of hierarchical resources using different aggregation strategies.
 * This class includes examples of retrieving resources with and without application IDs,
 * and using strategies to either find the first available resource or aggregate resources from all levels.
 */
public class HierarchicalResourceRetrieval {

    public static void main(String[] args) {

        // Create HierarchicalResourceRetrievalService and FooResource retrieval functions
        HierarchicalResourceRetrievalService retrievalService = new HierarchicalResourceRetrievalService();
        Function<String, Optional<String>> retrieveFooResource = new RetrieveFooResource();
        BiFunction<String, String, Optional<String>> retrieveFooResourceWithApp = new RetrieveFooResourceWithApp();

        // First-found resource strategy
        AggregationStrategy<String, String> firstFoundStrategy = new FirstFoundAggregationStrategy<>();
        String firstFoundResource =
                retrievalService.getHierarchicalResource("subChild", retrieveFooResource, firstFoundStrategy);
        System.out.println("First-found resource for Foo resource: " + firstFoundResource);

        // First-found resource strategy with application ID
        String firstFoundResourceWithApp =
                retrievalService.getHierarchicalResource("subChild", "subApp", retrieveFooResourceWithApp,
                        firstFoundStrategy);
        System.out.println("First-found resource for Foo resource with application ID: " + firstFoundResourceWithApp);

        // Aggregated resource strategy (combining resources from all levels)
        AggregationStrategy<String, List<String>> allLevelsStrategy = new AllLevelsAggregationStrategy<>();
        List<String> aggregatedResources =
                retrievalService.getHierarchicalResource("subChild", retrieveFooResource, allLevelsStrategy);
        System.out.println("Aggregated resource for Foo resource: " + aggregatedResources);

        // Aggregated resource strategy with application ID
        List<String> aggregatedResourcesWithApp =
                retrievalService.getHierarchicalResource("subChild", "subApp", retrieveFooResourceWithApp,
                        allLevelsStrategy);
        System.out.println("Aggregated resource for Foo resource with application ID: " + aggregatedResourcesWithApp);

    }
}

// New application and it's corresponding organization
class OrgApp {

    private String orgId;
    private String appId;

    public OrgApp(String orgId, String appId) {

        this.orgId = orgId;
        this.appId = appId;
    }

    public String getOrgId() {

        return orgId;
    }

    public String getAppId() {

        return appId;
    }

    @Override
    public String toString() {

        return "OrgApp{orgId='" + orgId + "', appId='" + appId + "'}";
    }
}

// Aggregation strategy interface that also defines the traversal mechanism.
interface AggregationStrategy<T, R> {

    R aggregate(List<String> orgHierarchy, Function<String, Optional<T>> resourceRetriever);

    // New method to handle OrgApp hierarchy
    R aggregate(List<OrgApp> orgAppHierarchy, BiFunction<String, String, Optional<T>> resourceRetriever);
}

// Strategy that returns the first found resource by traversing up the hierarchy.
class FirstFoundAggregationStrategy<T> implements AggregationStrategy<T, T> {

    @Override
    public T aggregate(List<String> orgHierarchy, Function<String, Optional<T>> resourceRetriever) {

        for (String orgId : orgHierarchy) {
            Optional<T> resource = resourceRetriever.apply(orgId);
            if (resource.isPresent()) {
                return resource.get();
            }
        }
        return resourceRetriever.apply(null).orElse(null);
    }

    @Override
    public T aggregate(List<OrgApp> orgAppHierarchy, BiFunction<String, String, Optional<T>> resourceRetriever) {

        for (OrgApp orgApp : orgAppHierarchy) {
            Optional<T> resource = resourceRetriever.apply(orgApp.getOrgId(), orgApp.getAppId());
            if (resource.isPresent()) {
                return resource.get();
            }
        }
        return resourceRetriever.apply(null, null).orElse(null);
    }
}

// Strategy that aggregates resources from all levels in the hierarchy.
class AllLevelsAggregationStrategy<T> implements AggregationStrategy<T, List<T>> {

    @Override
    public List<T> aggregate(List<String> orgHierarchy, Function<String, Optional<T>> resourceRetriever) {

        List<T> resources = new ArrayList<>();

        for (String orgId : orgHierarchy) {
            resourceRetriever.apply(orgId).ifPresent(resources::add);
        }

        if (resources.isEmpty()) {
            resourceRetriever.apply(null).ifPresent(resources::add);
        }

        return resources;
    }

    @Override
    public List<T> aggregate(List<OrgApp> orgAppHierarchy,
                             BiFunction<String, String, Optional<T>> resourceRetriever) {

        List<T> resources = new ArrayList<>();

        for (OrgApp orgApp : orgAppHierarchy) {
            resourceRetriever.apply(orgApp.getOrgId(), orgApp.getAppId()).ifPresent(resources::add);
        }

        if (resources.isEmpty()) {
            resourceRetriever.apply(null, null).ifPresent(resources::add);
            // resource
        }

        return resources;
    }
}

// HierarchicalResourceRetrievalService that delegates the resource retrieval to the strategy.
class HierarchicalResourceRetrievalService {

    /**
     * Generic method to retrieve or aggregate resources in an organization hierarchy.
     *
     * @param orgId                 The starting organization ID.
     * @param resourceRetriever     The resource specific retrieval function.
     * @param aggregationStrategy   Strategy to define how resources should be aggregated.
     * @return The resource result, based on the aggregation strategy.
     */
    public <T, R> R getHierarchicalResource(String orgId, Function<String, Optional<T>> resourceRetriever,
                                            AggregationStrategy<T, R> aggregationStrategy) {

        List<String> orgHierarchy = constructOrgHierarchy(orgId);
        return aggregationStrategy.aggregate(orgHierarchy, resourceRetriever);
    }

    /**
     * Generic method to retrieve or aggregate resources in an organization hierarchy,
     * with an additional application ID.
     *
     * @param orgId               The starting organization ID.
     * @param applicationId       The application ID.
     * @param resourceRetriever        The resource specific retrieval function.
     * @param aggregationStrategy Strategy to define how resources should be aggregated.
     * @return The resource result, based on the aggregation strategy.
     */
    public <T, R> R getHierarchicalResource(String orgId, String applicationId,
                                            BiFunction<String, String, Optional<T>> resourceRetriever,
                                            AggregationStrategy<T, R> aggregationStrategy) {

        // Build application hierarchy based on the applicationId
        List<OrgApp> applicationHierarchy = constructApplicationHierarchy(applicationId);

        // You can use the applicationId in the logic if needed
        return aggregationStrategy.aggregate(applicationHierarchy, resourceRetriever);
    }

    // Example method to construct the organization hierarchy.
    private List<String> constructOrgHierarchy(String orgId) {
        // Example hierarchy construction, should be replaced with actual logic
        List<String> hierarchy = new ArrayList<>();
        if ("subChild".equals(orgId)) {
            hierarchy.add("subChild");
            hierarchy.add("child");
            hierarchy.add("root");
        } else if ("child".equals(orgId)) {
            hierarchy.add("child");
            hierarchy.add("root");
        } else if ("root".equals(orgId)) {
            hierarchy.add("root");
        }
        return hierarchy;
    }

    // Example method to construct the application hierarchy.
    private List<OrgApp> constructApplicationHierarchy(String applicationId) {
        // Example hierarchy construction, should be replaced with actual logic
        List<OrgApp> hierarchy = new ArrayList<>();
        if ("subApp".equals(applicationId)) {
            hierarchy.add(new OrgApp("subChild", "subApp"));
            hierarchy.add(new OrgApp("child", "app"));
            hierarchy.add(new OrgApp("root", "rootApp"));
        } else if ("app".equals(applicationId)) {
            hierarchy.add(new OrgApp("child", "app"));
            hierarchy.add(new OrgApp("root", "rootApp"));
        } else if ("rootApp".equals(applicationId)) {
            hierarchy.add(new OrgApp("root", "rootApp"));
        }
        return hierarchy;
    }
}

// Resource retrieval function with org based traversal only.
class RetrieveFooResource implements Function<String, Optional<String>> {

    @Override
    public Optional<String> apply(String orgId) {

        if (orgId == null) {
            return Optional.of("default_value_for_foo");
        }
        // Logic to retrieve Foo within the given organization.
        return Optional.empty(); // Placeholder example: no resource at this level
    }
}

// Resource retrieval function with org traversal and app based traversal.
class RetrieveFooResourceWithApp implements BiFunction<String, String, Optional<String>> {

    @Override
    public Optional<String> apply(String orgId, String appId) {

        if (orgId == null) {
            return Optional.of("default_value_for_foo_with_app");
        }
        // Logic to retrieve Foo within the given organization and application.
        return Optional.empty(); // Placeholder example: no resource at this level
    }
}