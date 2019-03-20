Feature: User can specify that a temporal date is more than, or the same as, a specified threshold

Background:
     Given the generation strategy is full
       And there is a field foo
       And foo is of type "temporal"
       And foo is anything but null


Scenario: User requires to create a temporal field with date (YYYY-MM-DD) values within a given month that are after or at a specified date
     Given foo is after or at 2018-01-01T00:00:00.000
       And the generator can generate at most 6 rows
     Then the following data should be generated:
       | foo                     |
       | 2018-01-01T00:00:00.000 |
       | 2019-01-01T00:00:00.000 |
       | 2020-01-01T00:00:00.000 |
       | 2021-01-01T00:00:00.000 |
       | 2022-01-01T00:00:00.000 |
       | 2023-01-01T00:00:00.000 |

Scenario: User requires to create a temporal field with date and time (YYYY-MM-DDTHH:MM:SS) values that are after or at specified date and time
     Given foo is after or at 2018-01-01T12:00:00.000
       And foo is in set:
       | 2016-01-01T12:00:00.000 |
       | 2017-01-01T12:00:00.000 |
       | 2018-01-01T12:00:00.000 |
       | 2019-01-01T12:00:00.000 |
       | 2019-01-01T12:01:05.000 |
       | 2020-01-01T12:01:00.000 |
     Then the following data should be generated:
       | foo                     |
       | 2018-01-01T12:00:00.000 |
       | 2019-01-01T12:00:00.000 |
       | 2019-01-01T12:01:05.000 |
       | 2020-01-01T12:01:00.000 |

Scenario: Running afterOrAt request that includes temporal field with date (YYYY-MM-DD) values that has invalid date should fail
     Given foo is after or at 2019-15-32T00:00:00.000
     Then I am presented with an error message
       And no data is created

Scenario: Running afterOrAt request that includes temporal field with date and time (YYYY-MM-DDTHH:MM:SS) values that has invalid time should fail
     Given foo is after or at 2018-10-01T25:25:05.000
     Then I am presented with an error message
       And no data is created

Scenario: Running afterOrAt request that includes temporal field with date and time (YYYY-MM-DDTHH:MM:SS) values that has invalid year should fail
     Given foo is after or at 0000-01-10T00:00:00.000
     Then I am presented with an error message
       And no data is created

Scenario: Running beforeOrAt request that includes temporal field with date and time (YYYY-MM-DDTHH:MM:SS) values that has invalid format should fail
     Given foo is after or at "2018-Jan-31stT00:00:00.000"
     Then I am presented with an error message
       And no data is created


Scenario: Running afterOrAt request that includes temporal field with date and time (YYYY-MM-DDTHH:MM:SS) values that has leap year
     Given foo is after or at 2019-01-01T00:00:00.000
       And foo is in set:
       | 2017-01-01T00:00:00.000 |
       | 2018-01-01T00:00:00.000 |
       | 2019-01-01T00:00:00.000 |
       | 2020-01-01T00:00:00.000 |
       | 2021-01-01T00:00:00.000 |
     Then the following data should be generated:
       | foo                     |
       | 2019-01-01T00:00:00.000 |
       | 2020-01-01T00:00:00.000 |
       | 2021-01-01T00:00:00.000 |


Scenario: Running afterOrAt request against a non-contradicting afterOrAt constraint should be successful
     Given foo is after or at 2019-01-01T00:00:00.000
       And foo is after or at 2020-01-01T00:00:00.000
       And foo is in set:
       | 2017-01-01T00:00:00.000 |
       | 2018-01-01T00:00:00.000 |
       | 2019-01-01T00:00:00.000 |
       | 2020-01-01T00:00:00.000 |
       | 2021-01-01T00:00:00.000 |
       | 2022-01-01T00:00:00.000 |
     Then the following data should be generated:
       | foo                     |
       | 2020-01-01T00:00:00.000 |
       | 2021-01-01T00:00:00.000 |
       | 2022-01-01T00:00:00.000 |

Scenario: Running afterOrAt request against a non contradicting not afterOrAtOrAt should be successful
     Given foo is after or at 2019-01-01T00:00:00.000
       And foo is after or at 2020-01-01T00:00:00.000
       And foo is in set:
       | 2018-01-01T00:00:00.000 |
       | 2019-01-01T00:00:00.000 |
       | 2020-01-01T00:00:00.000 |
       | 2021-01-01T00:00:00.000 |
       | 2022-01-01T00:00:00.000 |
     Then the following data should be generated:
       | foo                     |
       | 2020-01-01T00:00:00.000 |
       | 2021-01-01T00:00:00.000 |
       | 2022-01-01T00:00:00.000 |

Scenario: Running afterOrAt request against a non contradicting beforeOrAt should be successful
     Given foo is after or at 2019-01-01T00:00:00.000
       And foo is before or at 2020-01-01T00:00:00.000
       And foo is in set:
       | 2017-01-01T00:00:00.000 |
       | 2018-01-01T00:00:00.000 |
       | 2019-01-01T00:00:00.000 |
       | 2020-01-01T00:00:00.000 |
     Then the following data should be generated:
       | foo                     |
       | 2019-01-01T00:00:00.000 |
       | 2020-01-01T00:00:00.000 |

Scenario: Running afterOrAt request against a non contradicting beforeOrAt should be successful
     Given foo is anything but after or at 2019-01-01T00:00:00.000
       And foo is before or at 2019-01-01T00:00:00.000
       And foo is in set:
       | 2017-01-01T00:00:00.000 |
       | 2018-01-01T00:00:00.000 |
       | 2019-01-01T00:00:00.000 |
       | 2020-01-01T00:00:00.000 |
     Then the following data should be generated:
       | foo                     |
       | 2017-01-01T00:00:00.000 |
       | 2018-01-01T00:00:00.000 |

Scenario: Running afterOrAt request against a non contradicting beforeOrAt should be successful
     Given foo is after or at 2019-01-01T00:00:00.000
       And foo is anything but before or at 2019-01-02T00:00:00.000
       And foo is in set:
       | 2017-01-01T00:00:00.000 |
       | 2018-01-01T00:00:00.000 |
       | 2019-01-01T00:00:00.000 |
       | 2020-01-01T00:00:00.000 |
       | 2021-01-01T00:00:00.000 |
     Then the following data should be generated:
       | foo                     |
       | 2020-01-01T00:00:00.000 |
       | 2021-01-01T00:00:00.000 |

Scenario: Running afterOrAt request against a non contradicting beforeOrAt should be successful
     Given foo is anything but after or at 2019-01-02T00:00:00.000
       And foo is before or at 2019-01-01T00:00:00.000
       And foo is in set:
       | 2017-01-01T00:00:00.000 |
       | 2018-01-01T00:00:00.000 |
       | 2019-01-01T00:00:00.000 |
       | 2020-01-01T00:00:00.000 |
       | 2021-01-01T00:00:00.000 |
     Then the following data should be generated:
       | foo                     |
       | 2017-01-01T00:00:00.000 |
       | 2018-01-01T00:00:00.000 |
       | 2019-01-01T00:00:00.000 |

Scenario: Running afterOrAt request against a non contradicting beforeOrAt should be successful
     Given foo is anything but after or at 2022-01-01T00:00:00.000
       And foo is anything but before or at 2019-01-01T00:00:00.000
       And foo is in set:
       | 2017-01-01T00:00:00.000 |
       | 2018-01-01T00:00:00.000 |
       | 2019-01-01T00:00:00.000 |
       | 2020-01-01T00:00:00.000 |
       | 2021-01-01T00:00:00.000 |
     Then the following data should be generated:
       | foo                     |
       | 2020-01-01T00:00:00.000 |
       | 2021-01-01T00:00:00.000 |


@ignore
Scenario: Running afterOrAt against a contradicting beforeOrAt should generate only string,numeric and null
     Given foo is after or at 2019-01-01T00:00:00.000
       And foo is before or at 2019-01-01T00:00:00.000
       And foo is in set:
       | 1                         |
       | 2018-01-01T00:00:00.000   |
       | 2011-01-01T00:00:00.000   |
       | "2011-01-01T00:00:00.000" |
       | "2018-01-01T00:00:00.000" |
       | 2018-01-01T00:00:00.000   |
     Then the following data should be generated:
       | foo  |
       | null |
       | 1    |
       | "2011-01-01T00:00:00.000" |
       | "2018-01-01T00:00:00.000" |


