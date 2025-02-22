* �����, ������!
* ������� �� ������� ���������! �� ����� ���������� � ��� ��������. �������, ����������.

����������-���� � �������������� Spring Boot � �������

����������:
1. ������ ���������:
    * 1.1. ������� 1: ��������� ����� MyblogBootApplication.
    * 1.2. ������� 2: ������� ������ � jar-���� ����� �� ������:
        * 1.2.1. `gradle clean build`;
        * 1.2.2. `./gradlew clean build`
    ������� � "build" (��������� �����) -> "libs" � ����������� ������ ���� ����� "myblog-boot-0.0.1-SNAPSHOT.jar". �
    ������� ������ �������: java -jar {������ ���� ����� "myblog-boot-0.0.1-SNAPSHOT.jar"}
    ����������: ���� � �������� ����� ���� ���� ��������, ���� ���� ����� ����� � ������� �������.
2. ������� �� ������: localhost:8080/feed. ������ �� ���������� �� �������� ����� � ������ ��������� ��������,
��������� ����.

� ���������� �����������:
1. ���������� ������ ����� (� ���������, ���������, �������, ������);
2. ��������� ������ � ����� (������ ��������� �� 10, 20 � 50 ������, ��������� ��������� �� 2 ����� - 
��� �������� ������������);
3. �������������� � �������� �����;
4. ����������� ������� ����� �����;
5. ����������� ��������� ����������� �����. � ����� - ������������� � ������� ����������� �����������.
6. ����� �� ���� (������ "#" ��� ���� ������� �������������).

�����������:
- �� ��������� ������ ���������� ������ �� ��������;
- ��� ���������� ������ ����� ���� ���������� ����������� ����� ������� (���� # � ���� ��� ���� ������������ -
��� ��� ���������� ��������� ���� ��� �������);
- ��� ����� ����� ������� ������������ � ����������� ������ ����������, ��������� � �������� ���� ���������� �������;
- ����������� ������������� ��������������� � ���� �����������. ��� ���������� ������������������ ����������� ����� 
������ ���������� Ctrl + Enter.

�����������, �������, �����������, ������ ������� ��������������� � ����-�������. ������������ �������� ����� ��� 
� �������������� Mock-�������� (����������� ������ ��������������� ���� ������������), ��� � ��� ��� (����������� 
����� ��� ����).
