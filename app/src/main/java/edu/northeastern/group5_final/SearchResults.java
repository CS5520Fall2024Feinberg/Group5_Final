package edu.northeastern.group5_final;

public class SearchResults {
    String firstName;
    String lastName;
    String username;
    String memberInfo;
    String profilePic;

    public SearchResults(String memberInfo, String username, String lastName, String firstName, String profilePic) {
        this.memberInfo = memberInfo;
        this.username = username;
        this.lastName = lastName;
        this.firstName = firstName;
        this.profilePic = profilePic;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getMemberInfo() {
        return memberInfo;
    }
    public String getProfilePic() {
        return profilePic;
    }
}
