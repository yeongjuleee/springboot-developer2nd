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