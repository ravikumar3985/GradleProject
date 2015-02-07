package com.ubs.opsit.interviews;
/**
 * Created by rpolipalli on 2/7/2015.
 */
import java.text.SimpleDateFormat;
import java.util.List;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.embedder.MetaFilter;
import org.jbehave.core.embedder.StoryManager;
import org.jbehave.core.failures.BatchFailures;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.parsers.RegexPrefixCapturingPatternParser;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.ParameterConverters.DateConverter;
import org.jbehave.core.steps.SilentStepMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.HTML;
import static org.jbehave.core.reporters.Format.TXT;
import static org.jbehave.core.reporters.Format.XML;
/**
* Specifies the Embedder for the BerlinClock example, providing the
* Configuration and the CandidateSteps, using classpath story loading.
*/
public class CoreEmbedder extends Embedder {

    private static final Logger LOG = LoggerFactory.getLogger(CoreEmbedder.class);

    /**
     * 
     * @return EmbedderControls
     */
@Override
    public EmbedderControls embedderControls() {
        return new EmbedderControls().doIgnoreFailureInStories(true).doIgnoreFailureInView(true);
    }

    /**
     *  
     * @return Configuration
     */
    @Override
    public Configuration configuration() {
     Class<? extends CoreEmbedder> embedderClass = this.getClass();
        return new MostUsefulConfiguration()
        .useStoryLoader(new LoadFromClasspath(embedderClass.getClassLoader()))
        .useStoryReporterBuilder(new StoryReporterBuilder()
                .withCodeLocation(CodeLocations.codeLocationFromClass(embedderClass))
                .withDefaultFormats()
                .withFormats(CONSOLE, TXT, HTML, XML)
                .withCrossReference(new CrossReference()))
        // use custom date pattern
        .useStepPatternParser(new RegexPrefixCapturingPatternParser(
                "%")) // use '%' instead of '$' to identify parameters
        .useStepMonitor(new SilentStepMonitor());
}

    /**
     *
     * @return
     */
    @Override
    public InjectableStepsFactory stepsFactory() {
     return new InstanceStepsFactory(configuration(),new BerlinClockFixture());
    }

    /**
     * 
     * @param storyPaths
     */
    @Override
    public void runStoriesAsPaths(List<String> storyPaths) {

        try {

            // set up run context
            StoryManager storyManager = createStoryManager();
            MetaFilter filter = metaFilter();
            BatchFailures failures = new BatchFailures();

            // run stories
            storyManager.runStories(storyPaths, filter, failures);

        }catch(Exception e) {
            LOG.debug("Exception Message :"+e.getMessage());
        }
    }

    /**
     * 
     * @return Strory Manager
     */
  private StoryManager createStoryManager() {
        return new StoryManager(configuration(), embedderControls(), embedderMonitor(), executorService(),
                stepsFactory(), storyRunner());
    }
}