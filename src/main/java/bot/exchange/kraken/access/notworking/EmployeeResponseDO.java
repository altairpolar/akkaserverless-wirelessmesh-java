package bot.exchange.kraken.access.notworking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
class EmployeeResponseDO {
    String id;
    String name;
    String salary;
    String age;
    String employee_name;
    String employee_salary;
    String employee_age;
    String profile_image;

}
