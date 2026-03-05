async function callApi(endpoint) {
    const responseEl = document.getElementById('response');
    responseEl.textContent = 'Loading...';
    responseEl.style.color = '#8b949e';

    try {
        const res = await fetch(endpoint);
        const text = await res.text();
        responseEl.textContent = `GET ${endpoint}\nStatus: ${res.status}\n\n${text}`;
        responseEl.style.color = res.ok ? '#7ee787' : '#f85149';
    } catch (err) {
        responseEl.textContent = `Error: ${err.message}`;
        responseEl.style.color = '#f85149';
    }
}
