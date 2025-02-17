����������-���� � �������������� Spring Framework � �������

����������:
1. � IDE ������� ������ ��������� "mvn clean install".
2. ������� ��������� ��������� "Tomcat". ��������� ���: ����� "bin" -> startup.
3. ��������� � �. 1 ������ myblog.war ����������� � ����� "Tomcat" "webapps". ����� ��������� ������ ��������� �����
���������� � ����� � ������.
4. � ����� "Tomcat" "conf" � ����� "tomcat-users" ����������������� ������������ � username = "admin".
5. ������� �� ������: localhost:8080/manager/html. ������ ����� "admin" � ������ "admin".
6. ������� �� ������: localhost:8080/myblog/feed. ������ �� ���������� �� �������� ����� � ������ ��������� ��������,
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
- ��� ���������� ������ ����� ���� ���������� ����������� ����� ������� (���� # � ���� ��� ���� ������������ -
��� ��� ���������� ��������� ���� ��� �������);
- ��� ����� ����� ������� ������������ � ����������� ������ ����������, ��������� � �������� ���� ���������� �������;
- ����������� ������������� ��������������� � ���� �����������. ��� ���������� ������������������ ����������� ����� ������ ���������� Ctrl + Enter

�����������, �������, �����������, ������ ������� ��������������� � ����-�������.

// ���� .jar ����������� � ������� � ��������� ������� ���� �����. ��� ����������� �������
// ���� ����� ������� "build" (��������� �����) -> "libs" -> "myblog-boot-0.0.1-SNAPSHOT.jar" � ����������� ������ ����.
// ����� - ������ � ������� �������:
// java -jar {������ ���� ����� "myblog-boot-0.0.1-SNAPSHOT.jar"}
����������: ���� � �������� ����� ���� ���� ��������, ���� ���� ����� ����� � ������� �������

// ������ "gradle clean build" (����� wrapper - "./gradlew clean build")
// ����: localhost:8080/feed
