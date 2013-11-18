/**
 * Created with IntelliJ IDEA.
 * User: b0b
 * Date: 11/16/13
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class RunnerBuilder {
    private String firstName;
    private String lastName = "";
    private String gender = "";
    private String phoneNum = "";

    public RunnerBuilder(){}

    public Runner BuildRunner(){
        return new Runner(firstName, lastName, gender, phoneNum);
    }

    public RunnerBuilder firstName(String firstName){
        this.firstName = firstName;
        return this;
    }

    public RunnerBuilder lastName(String lastName){
        this.lastName = lastName;
        return this;
    }
    public RunnerBuilder gender(String gender){
        this.gender = gender;
        return this;
    }
    public RunnerBuilder phoneNum(String phoneNum){
        this.phoneNum = phoneNum;
        return this;
    }
}
