-- 地区信息表
CREATE TABLE zhengzb_Region (
                                zzb_RegionID SERIAL PRIMARY KEY,
                                zzb_RegionName VARCHAR(50) NOT NULL UNIQUE
);

-- 专业信息表
CREATE TABLE zhengzb_Major (
                               zzb_MajorID SERIAL PRIMARY KEY,
                               zzb_MajorName VARCHAR(100) NOT NULL UNIQUE
);

-- 班级信息表
CREATE TABLE zhengzb_Class (
                               zzb_ClassID SERIAL PRIMARY KEY,
                               zzb_ClassName VARCHAR(50) NOT NULL,
                               zzb_MajorID INT NOT NULL,
                               FOREIGN KEY (zzb_MajorID) REFERENCES zhengzb_Major(zzb_MajorID)
);

-- 学生信息表
CREATE TABLE zhengzb_Student (
                                 zzb_Sno VARCHAR(20) PRIMARY KEY,
                                 zzb_Sname VARCHAR(50) NOT NULL,
                                 zzb_Sex CHAR(2) NOT NULL, CHECK ( zzb_Sex IN ('男', '女') ),
                                 zzb_Age INT,
                                 zzb_RegionID INT,
                                 zzb_CreditTotal DECIMAL(5,2) DEFAULT 0,
                                 zzb_ClassID INT NOT NULL,
                                 FOREIGN KEY (zzb_RegionID) REFERENCES zhengzb_Region(zzb_RegionID),
                                 FOREIGN KEY (zzb_ClassID) REFERENCES zhengzb_Class(zzb_ClassID)
);

-- 教师信息表
CREATE TABLE zhengzb_Teacher (
                                 zzb_Tno VARCHAR(20) PRIMARY KEY,
                                 zzb_Tname VARCHAR(50) NOT NULL,
                                 zzb_Tsex CHAR(2) NOT NULL,
                                 zzb_Tage INT,
                                 zzb_Title VARCHAR(50),
                                 zzb_Phone VARCHAR(20)
);

-- 课程信息表
CREATE TABLE zhengzb_Course (
                                zzb_Cno VARCHAR(20) PRIMARY KEY,
                                zzb_Cname VARCHAR(100) NOT NULL,
                                zzb_Hours INT,
                                zzb_Type VARCHAR(10), CHECK (zzb_Type IN ('考试', '考查')),
                                zzb_Credit DECIMAL(3,1), CHECK ( zzb_Credit >= 1 AND zzb_Credit <= 4 )
);

-- 课程开设表：多对多关系，关联课程、教师、班级、学期
CREATE TABLE zhengzb_CourseOffer (
                                     zzb_OfferID SERIAL PRIMARY KEY,
                                     zzb_Cno VARCHAR(20) NOT NULL,
                                     zzb_Tno VARCHAR(20) NOT NULL,
                                     zzb_ClassID INT NOT NULL,
                                     zzb_Term VARCHAR(20) NOT NULL,
                                     FOREIGN KEY (zzb_Cno) REFERENCES zhengzb_Course(zzb_Cno),
                                     FOREIGN KEY (zzb_Tno) REFERENCES zhengzb_Teacher(zzb_Tno),
                                     FOREIGN KEY (zzb_ClassID) REFERENCES zhengzb_Class(zzb_ClassID)
);

-- 学生成绩表（引用课程开设表）
CREATE TABLE zhengzb_Grade (
                               zzb_Sno VARCHAR(20) NOT NULL,
                               zzb_OfferID INT NOT NULL,
                               zzb_Score DECIMAL(5,2) CHECK (zzb_Score >= 0 AND zzb_Score <= 100),
                               PRIMARY KEY (zzb_Sno, zzb_OfferID),
                               FOREIGN KEY (zzb_Sno) REFERENCES zhengzb_Student(zzb_Sno),
                               FOREIGN KEY (zzb_OfferID) REFERENCES zhengzb_CourseOffer(zzb_OfferID)
);

-- 用户信息表：学生userid即为zzb_sno，学生username为zzb_Sname管理员自定义
CREATE TABLE zhengzb_User (
                              zzb_UserID VARCHAR(20) PRIMARY KEY,
                              zzb_Username VARCHAR(50) NOT NULL,
                              zzb_Password VARCHAR(100) NOT NULL,
                              zzb_Role VARCHAR(20) NOT NULL CHECK (zzb_Role IN ('student', 'admin', 'teacher'))
);


-- 视图：学生成绩查询视图
CREATE OR REPLACE VIEW v_StudentGrade AS
SELECT s.zzb_Sno, s.zzb_Sname, c.zzb_Cname, o.zzb_Term, g.zzb_Score, t.zzb_Tname, cl.zzb_ClassName, c.zzb_Credit
FROM zhengzb_Grade g
         JOIN zhengzb_Student s ON g.zzb_Sno = s.zzb_Sno
         JOIN zhengzb_CourseOffer o ON g.zzb_OfferID = o.zzb_OfferID
         JOIN zhengzb_Course c ON o.zzb_Cno = c.zzb_Cno
         JOIN zhengzb_Teacher t ON o.zzb_Tno = t.zzb_Tno
         JOIN zhengzb_Class cl ON o.zzb_ClassID = cl.zzb_ClassID;

-- 视图: 学生可选课程视图
CREATE OR REPLACE VIEW v_AvailableCourses AS
SELECT co.zzb_OfferID, cl.zzb_ClassID, c.zzb_Cno, c.zzb_Cname, c.zzb_Hours, c.zzb_Type, c.zzb_Credit, t.zzb_tname, co.zzb_term
FROM zhengzb_CourseOffer co
         JOIN zhengzb_Course c ON co.zzb_Cno = c.zzb_Cno
         JOIN zhengzb_Teacher t ON co.zzb_Tno = t.zzb_Tno
         JOIN zhengzb_Class cl ON co.zzb_ClassID = cl.zzb_ClassID;

-- 视图：学生已选课程视图
CREATE OR REPLACE VIEW v_SelectedCourses AS
SELECT o.zzb_OfferID, s.zzb_Sno, c.zzb_Cno, c.zzb_Cname, o.zzb_Term, t.zzb_Tname, c.zzb_Credit, g.zzb_Score
FROM zhengzb_Grade g
         JOIN zhengzb_Student s ON g.zzb_Sno = s.zzb_Sno
         JOIN zhengzb_CourseOffer o ON g.zzb_OfferID = o.zzb_OfferID
         JOIN zhengzb_Course c ON o.zzb_Cno = c.zzb_Cno
         JOIN zhengzb_Teacher t ON o.zzb_Tno = t.zzb_Tno;

-- 视图：教师任课查询视图
CREATE OR REPLACE VIEW v_TeacherCourse AS
SELECT o.zzb_OfferID, t.zzb_Tno, t.zzb_Tname, c.zzb_Cno, c.zzb_Cname, o.zzb_Term, cl.zzb_ClassName
FROM zhengzb_Teacher t
         JOIN zhengzb_CourseOffer o ON t.zzb_Tno = o.zzb_Tno
         JOIN zhengzb_Course c ON o.zzb_Cno = c.zzb_Cno
         JOIN zhengzb_Class cl ON o.zzb_ClassID = cl.zzb_ClassID;

-- 视图：生源地统计视图
CREATE OR REPLACE VIEW v_RegionStats AS
SELECT
    r.zzb_RegionName,
    COUNT(s.zzb_Sno) AS student_count
FROM
    zhengzb_Student s
JOIN
    zhengzb_Region r ON s.zzb_RegionID = r.zzb_RegionID
GROUP BY
    r.zzb_RegionName;

-- 成绩表索引
CREATE INDEX idx_grade_sno ON zhengzb_Grade(zzb_Sno);
CREATE INDEX idx_grade_offerid ON zhengzb_Grade(zzb_OfferID);
-- 学生表索引
CREATE INDEX idx_student_sno ON zhengzb_Student(zzb_Sno);
-- 教师表索引
CREATE INDEX idx_teacher_tno ON zhengzb_Teacher(zzb_Tno);
-- 用户表索引
CREATE INDEX idx_user_userid ON zhengzb_User(zzb_UserID);




-- 触发器：成绩插入或更新时自动更新学生已修学分总数
CREATE OR REPLACE FUNCTION update_credittotal() RETURNS TRIGGER AS $$
BEGIN
UPDATE zhengzb_Student
SET zzb_CreditTotal = (
    SELECT COALESCE(SUM(c.zzb_Credit),0)
    FROM zhengzb_Grade g
             JOIN zhengzb_CourseOffer o ON g.zzb_OfferID = o.zzb_OfferID
             JOIN zhengzb_Course c ON o.zzb_Cno = c.zzb_Cno
    WHERE g.zzb_Sno = NEW.zzb_Sno AND g.zzb_Score >= 60
)
WHERE zzb_Sno = NEW.zzb_Sno;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;
DROP TRIGGER IF EXISTS trg_update_credittotal ON zhengzb_Grade;
CREATE TRIGGER trg_update_credittotal
    AFTER INSERT OR UPDATE OF zzb_Score ON zhengzb_Grade
    FOR EACH ROW
    EXECUTE PROCEDURE update_credittotal();


-- 触发器：删除学生时自动删除相关记录
CREATE OR REPLACE FUNCTION delete_related_records() RETURNS TRIGGER AS $$
BEGIN
    -- 删除 `zhengzb_Grade` 表中与该学生相关的所有记录
    DELETE FROM zhengzb_Grade WHERE zzb_Sno = OLD.zzb_Sno;

    -- 删除 `zhengzb_User` 表中与该学生相关的记录（假设 zzb_UserID 与 zzb_Sno 相同）
    DELETE FROM zhengzb_User WHERE zzb_UserID = OLD.zzb_Sno;

    -- 返回旧记录
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;
-- 创建触发器
CREATE TRIGGER trg_delete_student
AFTER DELETE ON zhengzb_Student
FOR EACH ROW
EXECUTE PROCEDURE delete_related_records();



-- 删除已有的成绩表外键约束
ALTER TABLE zhengzb_Grade
DROP CONSTRAINT IF EXISTS zhengzb_grade_zzb_sno_fkey;
-- 重新创建成绩表外键约束（包含级联删除）
ALTER TABLE zhengzb_Grade
ADD CONSTRAINT zhengzb_grade_zzb_sno_fkey
FOREIGN KEY (zzb_sno) REFERENCES zhengzb_Student(zzb_sno)
ON DELETE CASCADE ON UPDATE CASCADE;
-- 删除已有的用户表学生外键约束
ALTER TABLE zhengzb_User
DROP CONSTRAINT IF EXISTS fk_user_student;
-- 删除已有的用户表教师外键约束
ALTER TABLE zhengzb_User
DROP CONSTRAINT IF EXISTS fk_user_teacher;


-- 创建教师更新触发器函数
CREATE OR REPLACE FUNCTION update_teacher_tno_cascade()
RETURNS TRIGGER AS $$
BEGIN
    -- 先允许教师表更新教师编号（通过返回NEW）
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
-- 创建教师更新后的触发器函数
CREATE OR REPLACE FUNCTION after_update_teacher_tno()
RETURNS TRIGGER AS $$
BEGIN
    -- 更新课程开设表
    UPDATE zhengzb_CourseOffer
    SET zzb_tno = NEW.zzb_tno
    WHERE zzb_tno = OLD.zzb_tno;

    -- 更新用户表
    UPDATE zhengzb_User
    SET zzb_UserID = NEW.zzb_tno
    WHERE zzb_UserID = OLD.zzb_tno AND zzb_Role = 'teacher';

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;
-- 创建BEFORE触发器
CREATE TRIGGER trg_teacher_tno_update
BEFORE UPDATE OF zzb_tno ON zhengzb_teacher
FOR EACH ROW
EXECUTE PROCEDURE update_teacher_tno_cascade();
-- 创建AFTER触发器
CREATE TRIGGER trg_after_teacher_tno_update
AFTER UPDATE OF zzb_tno ON zhengzb_teacher
FOR EACH ROW
EXECUTE PROCEDURE after_update_teacher_tno();
-- 删除已有的外键约束
ALTER TABLE zhengzb_Grade
DROP CONSTRAINT IF EXISTS zhengzb_grade_zzb_sno_fkey;
-- 只保留一次添加
ALTER TABLE zhengzb_Grade
ADD CONSTRAINT zhengzb_grade_zzb_sno_fkey
FOREIGN KEY (zzb_sno) REFERENCES zhengzb_Student(zzb_sno)
ON DELETE CASCADE ON UPDATE CASCADE;


-- 触发器函数：当学生表学号更新时，同步更新用户表的用户ID
CREATE OR REPLACE FUNCTION sync_student_user_id()
RETURNS TRIGGER AS $$
BEGIN
    -- 当学生表学号更新时，自动更新用户表的用户ID
    IF NEW.zzb_sno <> OLD.zzb_sno THEN
        UPDATE zhengzb_User
        SET zzb_UserID = NEW.zzb_sno
        WHERE zzb_UserID = OLD.zzb_sno;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
-- 删除已有触发器
DROP TRIGGER IF EXISTS trg_sync_student_user_id ON zhengzb_student;
CREATE TRIGGER trg_sync_student_user_id
AFTER UPDATE OF zzb_sno ON zhengzb_student
FOR EACH ROW
EXECUTE PROCEDURE sync_student_user_id();
-- 删除已有的用户表学生外键约束
ALTER TABLE zhengzb_User
DROP CONSTRAINT IF EXISTS fk_user_student;
-- 重新创建用户表学生外键约束（先清理无效数据）
DELETE FROM zhengzb_User
WHERE zzb_Role = 'student' AND zzb_UserID NOT IN (
    SELECT zzb_Sno FROM zhengzb_Student
);
-- 重新创建约束（确保只创建一次）
ALTER TABLE zhengzb_User
ADD CONSTRAINT fk_user_student
FOREIGN KEY (zzb_UserID) REFERENCES zhengzb_Student(zzb_sno)
ON UPDATE CASCADE ON DELETE CASCADE
DEFERRABLE INITIALLY DEFERRED;

-- 存储过程：插入学生信息
CREATE OR REPLACE PROCEDURE insert_student(
    sno VARCHAR,
    sname VARCHAR,
    sex CHAR,
    age INT,
    region_id INT,
    class_id INT
) AS
BEGIN
    INSERT INTO zhengzb_Student (
        zzb_Sno,
        zzb_Sname,
        zzb_Sex,
        zzb_Age,
        zzb_RegionID,
        zzb_ClassID
    ) VALUES (
        sno,
        sname,
        sex,
        age,
        region_id,
        class_id
    );
END;


-- 存储过程：查询特定学生的信息
CREATE OR REPLACE FUNCTION query_student_info(sno VARCHAR)
RETURNS TABLE (
    zzb_Sno VARCHAR,
    zzb_Sname VARCHAR,
    zzb_Sex CHAR,
    zzb_Age INT,
    zzb_RegionID INT,
    zzb_CreditTotal DECIMAL,
    zzb_ClassID INT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        s.zzb_Sno,
        s.zzb_Sname,
        s.zzb_Sex,
        s.zzb_Age,
        s.zzb_RegionID,
        s.zzb_CreditTotal,
        s.zzb_ClassID
    FROM zhengzb_Student s
    WHERE s.zzb_Sno = sno;
END;
$$ LANGUAGE plpgsql;


-- 存储过程：更新学生信息
CREATE OR REPLACE PROCEDURE update_student(
    sno VARCHAR,
    sname VARCHAR,
    sex CHAR,
    age INT,
    region_id INT,
    class_id INT
) AS
BEGIN
    UPDATE zhengzb_Student
    SET
        zzb_Sname = sname,
        zzb_Sex = sex,
        zzb_Age = age,
        zzb_RegionID = region_id,
        zzb_ClassID = class_id
    WHERE zzb_Sno = sno;
END;

ALTER TABLE zhengzb_user
    DROP CONSTRAINT IF EXISTS fk_user_student;
ALTER TABLE zhengzb_user
    DROP CONSTRAINT IF EXISTS fk_user_teacher;




INSERT INTO zhengzb_Region (zzb_RegionName) VALUES
                                                ('北京'),
                                                ('上海'),
                                                ('广州'),
                                                ('深圳'),
                                                ('天津'),
                                                ('重庆'),
                                                ('杭州'),
                                                ('南京'),
                                                ('武汉'),
                                                ('成都'),
                                                ('西安'),
                                                ('苏州'),
                                                ('青岛'),
                                                ('沈阳'),
                                                ('大连'),
                                                ('厦门'),
                                                ('福州'),
                                                ('郑州'),
                                                ('长沙'),
                                                ('合肥');
INSERT INTO zhengzb_Major (zzb_MajorName) VALUES
                                                ('计算机科学与技术'),
                                                ('软件工程'),
                                                ('信息安全'),
                                                ('电子信息工程'),
                                                ('通信工程'),
                                                ('自动化'),
                                                ('机械设计制造及其自动化'),
                                                ('土木工程'),
                                                ('环境工程'),
                                                ('经济学'),
                                                ('金融学'),
                                                ('会计学'),
                                                ('市场营销'),
                                                ('国际贸易'),
                                                ('法学'),
                                                ('心理学');

INSERT INTO zhengzb_class (zzb_ClassName, zzb_MajorID) VALUES
                                                ('计算机科学与技术2302班', 1),
                                                ('软件工程2301班', 2),
                                                ('信息安全2303班', 3),
                                                ('电子信息工程2304班', 4),
                                                ('通信工程2305班', 5),
                                                ('自动化2306班', 6),
                                                ('机械设计制造及其自动化2307班', 7),
                                                ('土木工程2308班', 8),
                                                ('环境工程2309班', 9),
                                                ('经济学2302班', 10),
                                                ('金融学2302班', 11),
                                                ('会计学2302班', 12),
                                                ('市场营销2302班', 13),
                                                ('国际贸易2302班', 14),
                                                ('法学2302班', 15),
                                                ('心理学2302班', 16);

-- 地区表主键聚簇
CLUSTER zhengzb_Region USING zhengzb_region_pkey;

-- 专业表主键聚簇
CLUSTER zhengzb_Major USING zhengzb_major_pkey;

-- 班级表主键聚簇
CLUSTER zhengzb_Class USING zhengzb_class_pkey;

-- 学生表主键聚簇
CLUSTER zhengzb_Student USING zhengzb_student_pkey;

-- 教师表主键聚簇
CLUSTER zhengzb_Teacher USING zhengzb_teacher_pkey;

-- 课程表主键聚簇
CLUSTER zhengzb_Course USING zhengzb_course_pkey;

-- 课程开设表按学期聚簇
CREATE INDEX idx_courseoffer_term ON zhengzb_CourseOffer(zzb_Term);
CLUSTER zhengzb_CourseOffer USING idx_courseoffer_term;

-- 学生成绩表按学号聚簇
CLUSTER zhengzb_Grade USING zhengzb_grade_pkey;

-- 用户表主键聚簇
CLUSTER zhengzb_User USING zhengzb_user_pkey;