(function() {
    // ---------- CONFIGURATION ----------
    const BASE_URL = 'http://localhost:8080/notification-system/api/v1/notification';
    
    // DOM elements (logs)
    const userLogSelector = document.getElementById('userLogSelector');
    const fetchLogsBtn = document.getElementById('fetchLogsBtn');
    const refreshLogsBtn = document.getElementById('refreshLogsBtn');
    const logListContainer = document.getElementById('logListContainer');
    const userIdBadge = document.getElementById('userIdBadge');
    
    // DOM elements (creator)
    const createUserSelect = document.getElementById('createUserSelect');
    const createTopicSelect = document.getElementById('createTopicSelect');
    const createMessageText = document.getElementById('createMessageText');
    const submitBtn = document.getElementById('submitNotificationBtn');
    const clearFormBtn = document.getElementById('clearFormBtn');
    const formStatusMsg = document.getElementById('formStatusMsg');

    // Helper: show status in creator area
    function setFormStatus(message, isError = false) {
        formStatusMsg.innerHTML = message;
        formStatusMsg.style.color = isError ? '#b91c1c' : '#166534';
        formStatusMsg.style.background = isError ? '#fee2e2' : '#e6f7ec';
        setTimeout(() => {
            if (formStatusMsg.innerHTML === message) {
                // keep non-critical but auto clear after 4 secs but only if not changed
                setTimeout(() => {
                    if (formStatusMsg.innerHTML === message) {
                        formStatusMsg.innerHTML = 'Ready to create notification';
                        formStatusMsg.style.color = '#2c3e66';
                        formStatusMsg.style.background = '#f8fafc';
                    }
                }, 2800);
            }
        }, 3000);
    }

    // Helper: generic fetch with error handling
    async function fetchNotifications(userId) {
        if (!userId) return [];
        const url = `${BASE_URL}/${encodeURIComponent(userId)}`;
        try {
            const response = await fetch(url, {
                method: 'GET',
                headers: { 'Accept': 'application/json' }
            });
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            const data = await response.json();
            // data hopefully is an array of log messages (strings or objects)
            return data;
        } catch (err) {
            console.error('Fetch logs error:', err);
            throw err;
        }
    }

// render logs list as a table with all notification fields
function renderLogs(logsData, userId) {
    if (!logsData || !Array.isArray(logsData) || logsData.length === 0) {
        logListContainer.innerHTML = `<div class="empty-logs"> No notification logs found for user ${userId}. Create one using the form!</div>`;
        return;
    }
    
    // Create table element
    const table = document.createElement('table');
    table.className = 'logs-table';
    table.style.width = '100%';
    table.style.borderCollapse = 'collapse';
    table.style.fontSize = '0.85rem';
    
    // Create table header
    const thead = document.createElement('thead');
    const headerRow = document.createElement('tr');
    headerRow.style.backgroundColor = '#f1f5f9';
    headerRow.style.borderBottom = '2px solid #cbd5e1';
    
    const headers = [
        'User ID', 'User Name', 'User Email', 'User Phone', 'Topic Name', 'Channel Name',
        'Message', 'Timestamp'
    ];
    
    headers.forEach(headerText => {
        const th = document.createElement('th');
        th.textContent = headerText;
        th.style.padding = '12px 8px';
        th.style.textAlign = 'left';
        th.style.fontWeight = '600';
        th.style.color = '#1e293b';
        th.style.borderBottom = '2px solid #cbd5e1';
        headerRow.appendChild(th);
    });
    thead.appendChild(headerRow);
    table.appendChild(thead);
    
    // Create table body
    const tbody = document.createElement('tbody');
    
    logsData.forEach((item, idx) => {
        const row = document.createElement('tr');
        row.style.borderBottom = '1px solid #e2e8f0';
        row.style.transition = 'background 0.15s';
        
        // Add hover effect
        row.addEventListener('mouseenter', () => {
            row.style.backgroundColor = '#f8fafc';
        });
        row.addEventListener('mouseleave', () => {
            row.style.backgroundColor = 'transparent';
        });
        
        // Extract fields with fallbacks
        let userId_val = '';
        let userName = '';
        let userEmail = '';
        let userPhone = '';
        let topicName = '';
        let channelName = '';
        let message = '';
        let timestamp = '';
        
        if (typeof item === 'string') {
            // If it's a string, put it in message field
            message = item;
        } else if (item && typeof item === 'object') {
            // User fields
            userId_val = item.userId || '';
            userName = item.userName || '';
            userEmail = item.userEmail || '';
            userPhone = item.userPhone || '';
            
            // Topic fields
            topicName = item.topicName || '';
            
            // Channel fields
            channelName = item.channelName || '';
            
            // Message and timestamp
            message = item.message || '';
            timestamp = item.formattedTimestamp || '';
            
        } else {
            message = String(item);
        }
        
        // Create cells
        const cells = [
            userId_val, userName, userEmail, userPhone, topicName, channelName,
            message, timestamp
        ];
        
        cells.forEach((cellData, index) => {
            const td = document.createElement('td');
            td.style.padding = '10px 8px';
            td.style.verticalAlign = 'top';
            td.style.wordBreak = 'break-word';
            
            // Special handling for message column (make it wrap better)
            if (index === 8) { // Message column
                td.style.maxWidth = '300px';
                td.style.whiteSpace = 'normal';
                td.style.wordBreak = 'break-word';
            }
            
            // Special handling for timestamp
            if (index === 9) {
                td.style.whiteSpace = 'nowrap';
                td.style.fontFamily = 'monospace';
                td.style.fontSize = '0.75rem';
            }
            
            td.textContent = cellData !== undefined && cellData !== null ? String(cellData) : '—';
            row.appendChild(td);
        });
        
        tbody.appendChild(row);
    });
    
    table.appendChild(tbody);
    
    // Clear container and add table
    logListContainer.innerHTML = '';
    logListContainer.appendChild(table);
    
    // Add responsive overflow container
    logListContainer.style.overflow = 'auto';
    logListContainer.style.maxHeight = '600px';
    
    const infoDiv = document.createElement('div');
    infoDiv.style.marginTop = '12px';
    infoDiv.style.fontSize = '0.75rem';
    infoDiv.style.color = '#64748b';
    infoDiv.style.textAlign = 'center';
    infoDiv.innerHTML = `notifications ${logsData.length}`;
    logListContainer.appendChild(infoDiv);
}

    // simple escape to avoid XSS
    function escapeHtml(str) {
        if (!str) return '';
        return str.replace(/[&<>]/g, function(m) {
            if (m === '&') return '&amp;';
            if (m === '<') return '&lt;';
            if (m === '>') return '&gt;';
            return m;
        }).replace(/[\uD800-\uDBFF][\uDC00-\uDFFF]/g, function(c) {
            return c;
        });
    }

    // load and display logs with loading state
    let currentLoading = false;
    async function loadLogsForUser(userId) {
        if (!userId) {
            logListContainer.innerHTML = `<div class="empty-logs"> Please select a valid user</div>`;
            userIdBadge.innerText = `User: —`;
            return;
        }
        userIdBadge.innerText = `User: ${userId}`;
        // show loading indicator
        logListContainer.innerHTML = `<div class="empty-logs" style="display:flex; justify-content:center; gap:6px;"> Loading notifications for user ${userId}...</div>`;
        try {
            const logs = await fetchNotifications(userId);
            renderLogs(logs, userId);
        } catch (err) {
            let errorDetail = err.message;
            if (err.message.includes('Failed to fetch') || err.message.includes('NetworkError')) {
                errorDetail = 'Cannot reach backend. Make sure server runs on http://localhost:8080';
            }
            logListContainer.innerHTML = `<div class="empty-logs" style="color:#b91c1c;"> Failed to load logs: ${escapeHtml(errorDetail)}<br><span style="font-size:0.75rem;"> Verify CORS / backend availability</span></div>`;
            console.error(err);
        }
    }

    // refresh currently selected user
    function refreshCurrentLogs() {
        const selectedUserId = userLogSelector.value;
        if (selectedUserId) {
            loadLogsForUser(selectedUserId);
        } else {
            logListContainer.innerHTML = `<div class="empty-logs"> Select a user ID from dropdown</div>`;
        }
    }

    // attach events for logs section
    fetchLogsBtn.addEventListener('click', () => {
        refreshCurrentLogs();
    });
    refreshLogsBtn.addEventListener('click', () => {
        refreshCurrentLogs();
    });
    userLogSelector.addEventListener('change', () => {
        // optional but not auto load, user clicks load explicitly. but we can also preview? fine.
        const userId = userLogSelector.value;
        userIdBadge.innerText = `User: ${userId} (not loaded)`;
    });
    // initial badge
    userIdBadge.innerText = `User: ${userLogSelector.value}`;
    
    // ---------- CREATE NOTIFICATION (POST) ----------
    async function postNotification(userId, topicName, message) {
        if (!userId || !topicName || !message.trim()) {
            throw new Error('All fields are required: userId, topicName, message');
        }
        const payload = {
            userId: userId,
            topicName: topicName,
            messageContent: message.trim()
        };
        const postUrl = `${BASE_URL}`;  // POST to collection endpoint
        const response = await fetch(postUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(payload)
        });
        if (!response.ok) {
            let errorText = `HTTP ${response.status}`;
            try {
                const errBody = await response.text();
                errorText += ` - ${errBody.slice(0, 100)}`;
            } catch(e) {}
            throw new Error(errorText);
        }
        const result = await response.json();
        return result;
    }

    // submit handler for creator form
    async function handleCreateNotification() {
        const userId = createUserSelect.value;
        const topicName = createTopicSelect.value;
        const message = createMessageText.value;
        
        if (!message.trim()) {
            setFormStatus('Message cannot be empty', true);
            return;
        }
        if (!userId) {
            setFormStatus(' Please select a valid user', true);
            return;
        }
        // disable button while submitting
        submitBtn.disabled = true;
        const originalText = submitBtn.innerText;
        submitBtn.innerText = 'Sending ...';
        setFormStatus('Sending notification to server ...', false);
        try {
            const responseData = await postNotification(userId, topicName, message);
            setFormStatus(`Notification created successfully! Response: ${JSON.stringify(responseData).substring(0, 120)}`, false);
            // optionally clear message field (but keep for another creation)
            createMessageText.value = '';
            // after success, auto refresh logs if the same user is selected in viewer? optional enhancement
            const currentViewUserId = userLogSelector.value;
            if (currentViewUserId === userId) {
                // refresh automatically to include new message
                loadLogsForUser(userId);
            } else {
                setFormStatus(`Created! Switch viewer to user ${userId} to see logs.`, false);
            }
        } catch (err) {
            console.error('POST error:', err);
            let friendlyMsg = err.message;
            if (err.message.includes('Failed to fetch')) {
                friendlyMsg = 'Cannot connect to backend. Ensure localhost:8080 is running and CORS enabled.';
            }
            setFormStatus(` Creation failed: ${friendlyMsg}`, true);
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerText = originalText;
            // reset status auto after few seconds soft
            setTimeout(() => {
                if (formStatusMsg.innerHTML.includes('Creation failed') || formStatusMsg.innerHTML.includes('successfully')) {
                    if (!formStatusMsg.innerHTML.includes('failed')) {
                        // keep clarity but not necessary
                    }
                }
            }, 4000);
        }
    }

    // clear form fields (not user/category, only message and reset status)
    function clearCreatorForm() {
        createMessageText.value = '';
        // reset category combobox to INFO, but keep as default.
        createTopicSelect.selectedIndex = 0;
        setFormStatus('Form cleared. You can write a new message.', false);
        createMessageText.focus();
    }

    submitBtn.addEventListener('click', handleCreateNotification);
    clearFormBtn.addEventListener('click', clearCreatorForm);

    // additional: when page loads, show friendly message for logs.
    logListContainer.innerHTML = `<div class="empty-logs"> Select a User and click "Load messages".<br>Use the creator card to POST new notifications.</div>`;
        
    const creatorCard = document.getElementById('creatorCard');
    creatorCard.style.borderTop = '3px solid #3b82f6';
    
})();