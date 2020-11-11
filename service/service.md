- SQL Query를 쓰지 않는다.
- 비즈니스 로직을 적는다.
- req, res 그리고 status code등 HTTP 전송 계층의 정보는 취급하지 않는다.
- 클래스 생성자에 필요한 객체를 받아라. (DI, lib typedi)
```
export default class UserService {

async Signup(user) {
    const userRecord = await UserModel.create(user);
    const companyRecord = await CompanyModel.create(userRecord); // needs userRecord to have the database id 
    const salaryRecord = await SalaryModel.create(userRecord, companyRecord); // depends on user and company to be created

    ...whatever

    await EmailService.startSignupSequence(userRecord)

    ...do more stuff

    return { user: userRecord, company: companyRecord };
}
```