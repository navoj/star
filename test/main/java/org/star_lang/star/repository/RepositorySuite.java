package org.star_lang.star.repository;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ RepositoryTest.class, DirectoryRepoTest.class, CompositeRepositoryTest.class, TargetTest.class })
public class RepositorySuite
{

}
