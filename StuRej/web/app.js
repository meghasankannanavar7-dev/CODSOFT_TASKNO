const coursesElement = document.getElementById('courses');
const registeredElement = document.getElementById('registered');
const studentSelect = document.getElementById('studentId');
const statusElement = document.getElementById('status');

async function request(url, options) {
  const response = await fetch(url, options);
  const text = await response.text();
  if (!response.ok) throw new Error(text || 'Request failed.');
  const contentType = response.headers.get('content-type') || '';
  return contentType.includes('application/json') ? JSON.parse(text) : text;
}

function showStatus(message, type) {
  statusElement.textContent = message;
  statusElement.className = `status ${type || ''}`;
  window.setTimeout(() => { statusElement.textContent = ''; }, 4000);
}

function renderCourses(courses) {
  document.getElementById('courseCount').textContent = courses.length;
  coursesElement.innerHTML = courses.map(course => `
    <article class="course-card">
      <span class="code">${course.courseCode}</span>
      <h4>${course.title}</h4>
      <p class="description">${course.description}</p>
      <div class="meta"><span>${course.schedule}</span><span class="seats">${course.availableSeats} seats</span></div>
      <button class="register-button" data-course="${course.courseCode}" ${course.availableSeats === 0 ? 'disabled' : ''}>${course.availableSeats === 0 ? 'Course full' : 'Register for course  +'}</button>
    </article>`).join('');
  document.querySelectorAll('.register-button').forEach(button => button.addEventListener('click', () => register(button.dataset.course)));
}

async function loadCourses() {
  try { renderCourses(await request('/api/courses')); }
  catch (error) { coursesElement.innerHTML = '<div class="loading">Unable to load courses.</div>'; showStatus(error.message, 'error'); }
}

async function loadRegistered() {
  registeredElement.innerHTML = '<div class="loading">Loading registrations...</div>';
  try {
    const courses = await request(`/api/students/${studentSelect.value}/courses`);
    registeredElement.innerHTML = courses.length ? courses.map(course => `<div class="registered-row"><div><strong>${course.courseCode} · ${course.title}</strong><span>${course.schedule}</span></div><button class="drop-button" data-course="${course.courseCode}">Drop course</button></div>`).join('') : '<div class="loading">No courses registered.</div>';
    document.querySelectorAll('.drop-button').forEach(button => button.addEventListener('click', () => drop(button.dataset.course)));
  } catch (error) { registeredElement.innerHTML = '<div class="loading">Unable to load registrations.</div>'; showStatus(error.message, 'error'); }
}

async function register(courseCode) {
  try { showStatus(await request('/api/register', { method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({studentId: studentSelect.value, courseCode}) }), 'success'); await loadCourses(); await loadRegistered(); }
  catch (error) { showStatus(error.message, 'error'); }
}

async function drop(courseCode) {
  try { showStatus(await request('/api/drop', { method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({studentId: studentSelect.value, courseCode}) }), 'success'); await loadCourses(); await loadRegistered(); }
  catch (error) { showStatus(error.message, 'error'); }
}

studentSelect.addEventListener('change', loadRegistered);
document.getElementById('refresh').addEventListener('click', loadRegistered);
loadCourses();
loadRegistered();
