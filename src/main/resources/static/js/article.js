// 삭제 기능
const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', () => {
        /*
        원형 :
        deleteButton.addEventListener('click', event => {

        수정한 이유 : 따로 event 의 매개변수가 없기 때문에 제거 되어도 기능이 잘 작동되기 때문
         */
        let id = document.getElementById('article-id').value;
        fetch(`/api/articles/${id}`, {
            method: 'DELETE'
        })
            .then( () => {
                alert('삭제가 완료되었습니다.');
                location.replace('/articles');
            });
    });
}

// 수정 기능
// 1. id가 modify-btn인 엘리먼트 조회
const modifyButton = document.getElementById('modify-btn');

if(modifyButton) {
    // 2. 클릭 이벤트가 감지되면 수정 API 요청
    modifyButton.addEventListener('click', event => {
        // 요소가 제대로 조회되는지 확인하기 위한 변수 지정
        let titleElement = document.getElementById('title');
        console.log(titleElement)
        console.log(titleElement.value); // 요소가 제대로 조회되는지 확인하기

        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        fetch(`/api/articles/${id}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify( {
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        })
            .then( () => {
                alert('수정이 완료되었습니다.');
                location.replace(`/articles/${id}`);
            });
    });
}

// 생성(등록) 기능
// 1. id가 create-btn인 엘리먼트
const createButton = document.getElementById('create-btn');

if(createButton) {
    // 2. 클릭 이벤트가 감지되면 생성 API 요청
    createButton.addEventListener("click", () => {
        fetch("/api/articles", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify( {
                title: document.getElementById("title").value,
                content: document.getElementById("content").value,
            }),
        }). then( () => {
            alert("등록 완료되었습니다.");
            location.replace("/articles");
        });
    });
}
